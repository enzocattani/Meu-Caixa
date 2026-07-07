package com.meucaixa.app.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Agenda as notificações periódicas do app via WorkManager:
 * - lembrete diário (todo dia, sempre);
 * - aviso do 5º dia útil do mês (roda todo dia, mas só notifica quando a
 *   condição de data bate, ver [QuintoDiaUtilWorker]).
 *
 * O WorkManager persiste os agendamentos e os restaura sozinho após reboot
 * do aparelho, então não é necessário nenhum BroadcastReceiver de boot.
 */
object NotificationScheduler {

    private const val WORK_LEMBRETE_DIARIO = "work_lembrete_diario"
    private const val WORK_QUINTO_DIA_UTIL = "work_quinto_dia_util"

    /** Chamar uma vez ao iniciar o app (ex.: em Application.onCreate). */
    fun agendarNotificacoes(context: Context) {
        NotificationHelper.criarCanal(context)

        val workManager = WorkManager.getInstance(context)

        // Lembrete diário às 20h, lembrando de lançar as movimentações do dia.
        workManager.enqueueUniquePeriodicWork(
            WORK_LEMBRETE_DIARIO,
            ExistingPeriodicWorkPolicy.KEEP,
            criarRequisicaoDiaria<LembreteDiarioWorker>(horaAlvo = 20, minutoAlvo = 0)
        )

        // Checagem do 5º dia útil às 9h (só notifica quando a data bater).
        workManager.enqueueUniquePeriodicWork(
            WORK_QUINTO_DIA_UTIL,
            ExistingPeriodicWorkPolicy.KEEP,
            criarRequisicaoDiaria<QuintoDiaUtilWorker>(horaAlvo = 9, minutoAlvo = 0)
        )
    }

    private inline fun <reified T : ListenableWorker> criarRequisicaoDiaria(
        horaAlvo: Int,
        minutoAlvo: Int
    ): PeriodicWorkRequest {
        val atrasoInicial = calcularAtrasoInicial(horaAlvo, minutoAlvo)
        return PeriodicWorkRequestBuilder<T>(1, TimeUnit.DAYS)
            .setInitialDelay(atrasoInicial, TimeUnit.MILLISECONDS)
            .build()
    }

    /** Calcula quantos ms faltam até o próximo horário-alvo (hoje, ou amanhã se já passou). */
    private fun calcularAtrasoInicial(horaAlvo: Int, minutoAlvo: Int): Long {
        val agora = LocalDateTime.now()
        var alvo = agora.withHour(horaAlvo).withMinute(minutoAlvo).withSecond(0).withNano(0)
        if (!alvo.isAfter(agora)) {
            alvo = alvo.plusDays(1)
        }
        return Duration.between(agora, alvo).toMillis()
    }
}
