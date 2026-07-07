package com.meucaixa.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.meucaixa.app.R

/**
 * Centraliza a criação do canal de notificações e o disparo das notificações
 * push locais do app (lembrete diário e aviso do 5º dia útil do mês).
 */
object NotificationHelper {

    const val CANAL_LEMBRETES = "lembretes_meu_caixa"

    private const val ID_LEMBRETE_DIARIO = 1001
    private const val ID_QUINTO_DIA_UTIL = 1002

    /** Cria o canal de notificação (idempotente, seguro chamar sempre que o app inicia). */
    fun criarCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_LEMBRETES,
                context.getString(R.string.notif_canal_nome),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notif_canal_descricao)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(canal)
        }
    }

    /** Notificação diária lembrando de lançar as movimentações do dia. */
    fun notificarLembreteDiario(context: Context) {
        notificar(
            context = context,
            id = ID_LEMBRETE_DIARIO,
            titulo = context.getString(R.string.notif_lembrete_diario_titulo),
            mensagem = context.getString(R.string.notif_lembrete_diario_texto)
        )
    }

    /** Notificação disparada apenas no 5º dia útil do mês. */
    fun notificarQuintoDiaUtil(context: Context) {
        notificar(
            context = context,
            id = ID_QUINTO_DIA_UTIL,
            titulo = context.getString(R.string.notif_quinto_dia_util_titulo),
            mensagem = context.getString(R.string.notif_quinto_dia_util_texto)
        )
    }

    private fun notificar(context: Context, id: Int, titulo: String, mensagem: String) {
        // A partir do Android 13 (API 33), a permissão POST_NOTIFICATIONS precisa
        // ter sido concedida em tempo de execução, ou a notificação não aparece.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Usa o launcher intent do próprio app para abrir a tela principal ao tocar
        // na notificação, sem depender diretamente da classe MainActivity.
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacao = NotificationCompat.Builder(context, CANAL_LEMBRETES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensagem))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacao)
    }
}
