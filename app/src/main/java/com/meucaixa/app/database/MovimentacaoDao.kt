package com.meucaixa.app.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meucaixa.app.model.Movimentacao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object da tabela `movimentacoes`.
 *
 * Consultas que alimentam a UI em tempo real retornam [Flow], para que a tela
 * seja recomposta automaticamente sempre que os dados mudarem no banco.
 * Operações de escrita são `suspend`, para serem chamadas a partir de
 * corrotinas sem bloquear a thread principal.
 */
@Dao
interface MovimentacaoDao {

    @Insert
    suspend fun inserir(movimentacao: Movimentacao): Long

    @Insert
    suspend fun inserirTodas(movimentacoes: List<Movimentacao>)

    @Update
    suspend fun atualizar(movimentacao: Movimentacao)

    @Delete
    suspend fun excluir(movimentacao: Movimentacao)

    @Query("SELECT * FROM movimentacoes WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): Movimentacao?

    @Query("SELECT * FROM movimentacoes ORDER BY data DESC, dataCriacao DESC")
    fun observarTodas(): Flow<List<Movimentacao>>

    @Query("SELECT * FROM movimentacoes WHERE data = :data ORDER BY dataCriacao ASC")
    fun observarPorData(data: LocalDate): Flow<List<Movimentacao>>

    @Query("SELECT * FROM movimentacoes WHERE data BETWEEN :inicio AND :fim ORDER BY data ASC, dataCriacao ASC")
    fun observarPorPeriodo(inicio: LocalDate, fim: LocalDate): Flow<List<Movimentacao>>

    @Query("SELECT * FROM movimentacoes ORDER BY data DESC, dataCriacao DESC")
    suspend fun listarTodasUmaVez(): List<Movimentacao>

    @Query("DELETE FROM movimentacoes")
    suspend fun limparTudo()

    @Query("SELECT COUNT(*) FROM movimentacoes")
    suspend fun contarRegistros(): Int
}
