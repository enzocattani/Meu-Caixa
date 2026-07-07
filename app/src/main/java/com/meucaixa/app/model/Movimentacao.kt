package com.meucaixa.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidade principal do aplicativo: representa um único lançamento financeiro
 * (uma entrada ou uma saída) ocorrido em um dia específico.
 *
 * O valor monetário é armazenado em centavos ([valorCentavos]) para evitar
 * problemas de arredondamento de ponto flutuante em cálculos financeiros.
 *
 * @property id identificador único gerado automaticamente pelo Room.
 * @property tipo se é uma entrada ou uma saída.
 * @property valorCentavos valor da movimentação, em centavos (ex.: R$ 120,50 = 12050).
 * @property formaPagamento forma de pagamento utilizada.
 * @property descricao texto livre e opcional descrevendo a movimentação.
 * @property data dia em que a movimentação ocorreu.
 * @property dataCriacao instante (epoch millis) em que o registro foi criado, usado
 * para ordenar lançamentos do mesmo dia na ordem em que foram cadastrados.
 */
@Entity(tableName = "movimentacoes")
data class Movimentacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tipo: TipoMovimentacao,
    val valorCentavos: Long,
    val formaPagamento: FormaPagamento,
    val descricao: String = "",
    val data: LocalDate,
    val dataCriacao: Long = System.currentTimeMillis()
) {
    /** Valor com sinal: positivo para entradas, negativo para saídas. Útil para somas de saldo. */
    val valorComSinal: Long
        get() = if (tipo == TipoMovimentacao.ENTRADA) valorCentavos else -valorCentavos
}
