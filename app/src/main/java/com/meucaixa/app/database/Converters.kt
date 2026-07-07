package com.meucaixa.app.database

import androidx.room.TypeConverter
import com.meucaixa.app.model.FormaPagamento
import com.meucaixa.app.model.TipoMovimentacao
import java.time.LocalDate

/**
 * Conversores usados pelo Room para persistir tipos que não são suportados
 * nativamente pelo SQLite: [LocalDate] e os enums do domínio.
 *
 * Datas são guardadas como um inteiro no formato epoch day ([LocalDate.toEpochDay]),
 * o que permite comparações e ordenações (BETWEEN, ORDER BY) diretamente em SQL.
 */
class Converters {

    @TypeConverter
    fun fromEpochDay(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun toEpochDay(data: LocalDate?): Long? {
        return data?.toEpochDay()
    }

    @TypeConverter
    fun fromTipoMovimentacaoNome(nome: String?): TipoMovimentacao? {
        return nome?.let { TipoMovimentacao.valueOf(it) }
    }

    @TypeConverter
    fun toTipoMovimentacaoNome(tipo: TipoMovimentacao?): String? {
        return tipo?.name
    }

    @TypeConverter
    fun fromFormaPagamentoNome(nome: String?): FormaPagamento? {
        return nome?.let { FormaPagamento.valueOf(it) }
    }

    @TypeConverter
    fun toFormaPagamentoNome(forma: FormaPagamento?): String? {
        return forma?.name
    }
}
