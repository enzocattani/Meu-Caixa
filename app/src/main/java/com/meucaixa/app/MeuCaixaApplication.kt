package com.meucaixa.app

import android.app.Application
import com.meucaixa.app.database.MeuCaixaDatabase
import com.meucaixa.app.notifications.NotificationScheduler
import com.meucaixa.app.repository.MovimentacaoRepository

/**
 * Classe Application do Meu Caixa.
 *
 * Mantém uma única instância do banco de dados e do repositório durante
 * todo o ciclo de vida do app, seguindo o padrão de container de dependências manual
 * (evitando a necessidade de um framework de injeção de dependência para um app deste porte).
 */
class MeuCaixaApplication : Application() {

    val database: MeuCaixaDatabase by lazy {
        MeuCaixaDatabase.getInstance(this)
    }

    val repository: MovimentacaoRepository by lazy {
        MovimentacaoRepository(database.movimentacaoDao())
    }

    override fun onCreate() {
        super.onCreate()
        NotificationScheduler.agendarNotificacoes(this)
    }
}
