package com.meucaixa.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Dispara todo dia uma notificação lembrando o usuário de lançar as
 * movimentações do caixa. Não depende de nenhuma condição de data.
 */
class LembreteDiarioWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.notificarLembreteDiario(applicationContext)
        return Result.success()
    }
}
