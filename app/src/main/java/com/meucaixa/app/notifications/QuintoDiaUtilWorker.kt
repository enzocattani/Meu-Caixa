package com.meucaixa.app.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meucaixa.app.utils.DateUtils
import java.time.LocalDate

/**
 * Roda todo dia, mas só dispara a notificação quando a data de hoje
 * coincidir com o 5º dia útil do mês (considerando dias úteis como
 * segunda a sexta, sem levar feriados em conta).
 */
class QuintoDiaUtilWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hoje = LocalDate.now()
        if (DateUtils.ehQuintoDiaUtilDoMes(hoje)) {
            NotificationHelper.notificarQuintoDiaUtil(applicationContext)
        }
        return Result.success()
    }
}
