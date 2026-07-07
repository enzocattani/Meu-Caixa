package com.meucaixa.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meucaixa.app.model.Movimentacao

/**
 * Banco de dados Room do Meu Caixa.
 *
 * Utiliza o padrão Singleton (double-checked locking) para garantir que
 * apenas uma instância do banco exista durante todo o ciclo de vida do app,
 * evitando problemas de concorrência e uso desnecessário de memória.
 */
@Database(
    entities = [Movimentacao::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MeuCaixaDatabase : RoomDatabase() {

    abstract fun movimentacaoDao(): MovimentacaoDao

    companion object {
        private const val NOME_BANCO = "meu_caixa.db"

        @Volatile
        private var instancia: MeuCaixaDatabase? = null

        /** Nome do arquivo físico do banco, usado também pelas rotinas de backup/restauração. */
        const val NOME_ARQUIVO_BANCO = NOME_BANCO

        /**
         * Retorna a instância única do banco de dados, criando-a na primeira
         * chamada de forma segura para múltiplas threads.
         */
        fun getInstance(context: Context): MeuCaixaDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: construirBanco(context).also { instancia = it }
            }
        }

        private fun construirBanco(context: Context): MeuCaixaDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                MeuCaixaDatabase::class.java,
                NOME_BANCO
            ).build()
        }

        /**
         * Fecha e libera a instância atual do banco. Usado pela rotina de
         * restauração de backup, que precisa substituir o arquivo físico do
         * banco enquanto nenhuma conexão estiver aberta sobre ele.
         */
        fun fecharInstancia() {
            synchronized(this) {
                instancia?.close()
                instancia = null
            }
        }
    }
}
