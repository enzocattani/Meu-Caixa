package com.meucaixa.app.repository

import com.meucaixa.app.database.MovimentacaoDao
import com.meucaixa.app.model.EstatisticasMes
import com.meucaixa.app.model.Movimentacao
import com.meucaixa.app.model.ResumoDia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth

/**
 * Camada única de acesso a dados de movimentações, isolando o restante do
 * aplicativo (ViewModels, PDF, backup) dos detalhes de implementação do Room.
 *
 * Todas as consultas reativas para a UI são expostas como [Flow]; operações
 * de escrita são funções `suspend` que devem ser chamadas de dentro de um
 * `viewModelScope` ou outra corrotina apropriada.
 */
class MovimentacaoRepository(private val dao: MovimentacaoDao) {

    /** Observa todas as movimentações já cadastradas, mais recentes primeiro. */
    fun observarTodas(): Flow<List<Movimentacao>> = dao.observarTodas()

    /** Observa as movimentações de um único dia. */
    fun observarPorDia(data: LocalDate): Flow<List<Movimentacao>> = dao.observarPorData(data)

    /** Observa as movimentações de um mês inteiro (usado pelo calendário e pelo dashboard). */
    fun observarPorMes(mes: YearMonth): Flow<List<Movimentacao>> {
        return dao.observarPorPeriodo(mes.atDay(1), mes.atEndOfMonth())
    }

    /** Observa as movimentações dentro de um intervalo de datas arbitrário. */
    fun observarPorPeriodo(inicio: LocalDate, fim: LocalDate): Flow<List<Movimentacao>> {
        return dao.observarPorPeriodo(inicio, fim)
    }

    /** Observa diretamente os marcadores agregados de cada dia de um mês, prontos para o calendário. */
    fun observarResumoPorDia(mes: YearMonth): Flow<Map<LocalDate, ResumoDia>> {
        return observarPorMes(mes).map { movimentacoes -> ResumoDia.agruparPorDia(movimentacoes) }
    }

    /** Observa as estatísticas completas de um mês, prontas para o Dashboard e os gráficos. */
    fun observarEstatisticasDoMes(mes: YearMonth): Flow<EstatisticasMes> {
        return observarPorMes(mes).map { movimentacoes -> EstatisticasMes.calcular(mes, movimentacoes) }
    }

    /**
     * Salva uma movimentação: insere um novo registro se [movimentacao.id] for zero,
     * ou atualiza o registro existente caso contrário.
     *
     * @return o id do registro salvo.
     */
    suspend fun salvar(movimentacao: Movimentacao): Long {
        return if (movimentacao.id == 0L) {
            dao.inserir(movimentacao)
        } else {
            dao.atualizar(movimentacao)
            movimentacao.id
        }
    }

    /** Exclui uma movimentação existente. */
    suspend fun excluir(movimentacao: Movimentacao) {
        dao.excluir(movimentacao)
    }

    /**
     * Desfaz a exclusão de uma movimentação, reinserindo-a com um novo id
     * (usado pela ação "Desfazer" do Snackbar exibido após excluir um item).
     */
    suspend fun restaurar(movimentacao: Movimentacao): Long {
        return dao.inserir(movimentacao.copy(id = 0L))
    }

    /** Cria uma cópia independente de uma movimentação existente, datada de hoje por padrão. */
    suspend fun duplicar(movimentacao: Movimentacao, novaData: LocalDate = movimentacao.data): Long {
        return dao.inserir(
            movimentacao.copy(id = 0L, data = novaData, dataCriacao = System.currentTimeMillis())
        )
    }

    /** Busca uma movimentação específica pelo id, ou nulo se não existir. */
    suspend fun buscarPorId(id: Long): Movimentacao? = dao.buscarPorId(id)

    /** Lista todas as movimentações de uma só vez, sem observação contínua (usado no backup/PDF). */
    suspend fun listarTodas(): List<Movimentacao> = dao.listarTodasUmaVez()

    /** Apaga permanentemente todos os registros do banco (usado antes de restaurar um backup). */
    suspend fun limparTudo() = dao.limparTudo()

    /** Insere em lote uma lista de movimentações (usado na restauração de backup/importação). */
    suspend fun importarTodas(movimentacoes: List<Movimentacao>) {
        dao.limparTudo()
        dao.inserirTodas(movimentacoes)
    }

    /** Retorna verdadeiro se já existe pelo menos uma movimentação cadastrada. */
    suspend fun possuiDados(): Boolean = dao.contarRegistros() > 0
}
