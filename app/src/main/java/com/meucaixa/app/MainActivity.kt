package com.meucaixa.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.meucaixa.app.ui.theme.MeuCaixaTheme

/**
 * Activity principal do Meu Caixa.
 *
 * IMPORTANTE: esta versão é um placeholder mínimo, criado apenas para o app
 * compilar e poder pedir a permissão de notificação em runtime. As telas
 * reais (lançamentos, extrato, gráficos, PDF etc.) e a navegação NÃO
 * estavam presentes no projeto recebido (pastas ui/screens, navigation,
 * viewmodel, pdf e charts vieram vazias) e precisam ser adicionadas aqui.
 */
class MainActivity : ComponentActivity() {

    // Launcher que dispara o diálogo do sistema pedindo a permissão de notificação.
    private val solicitarPermissaoNotificacao = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Independentemente do resultado (concedida ou negada), o app segue
        // funcionando normalmente. Sem a permissão, apenas as notificações
        // push (lembrete diário e aviso do 5º dia útil) não serão exibidas.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pedirPermissaoNotificacaoSeNecessario()

        setContent {
            MeuCaixaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TelaPrincipalPlaceholder()
                }
            }
        }
    }

    /**
     * No Android 13 (API 33) em diante, exibir notificações exige a permissão
     * em runtime android.permission.POST_NOTIFICATIONS. Em versões
     * anteriores essa permissão é concedida automaticamente na instalação,
     * então a checagem/solicitação só é necessária a partir do Android 13.
     */
    private fun pedirPermissaoNotificacaoSeNecessario() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val jaTemPermissao = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!jaTemPermissao) {
                solicitarPermissaoNotificacao.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
private fun TelaPrincipalPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Meu Caixa\n\nTela principal ainda não adicionada a este projeto.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
