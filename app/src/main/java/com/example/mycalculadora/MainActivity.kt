package com.example.mycalculadora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Calculadora()
                }
            }
        }
    }
}

// Lógica matemática: o when funciona como um switch
fun calcular(a: Double, b: Double, op: String): Double {
    return when (op) {
        "+" -> a + b
        "-" -> a - b
        "×" -> a * b
        else -> a / b
    }
}

// Mostra 5 em vez de 5.0, sem perder casas decimais
fun formatar(valor: Double): String {
    var texto = String.format(Locale.US, "%.10f", valor)
    while (texto.endsWith("0")) {
        texto = texto.dropLast(1)
    }
    if (texto.endsWith(".")) {
        texto = texto.dropLast(1)
    }
    if (texto == "-0") {
        texto = "0"
    }
    return texto
}

@Composable
fun Tecla(texto: String, modifier: Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(72.dp)) {
        Text(texto, fontSize = 24.sp)
    }
}

@Composable
fun Calculadora() {
    var visor by rememberSaveable { mutableStateOf("0") }
    var valorAnterior by rememberSaveable { mutableStateOf(0.0) }
    var operador by rememberSaveable { mutableStateOf("") }
    var novoNumero by rememberSaveable { mutableStateOf(true) }

    fun limpar() {
        visor = "0"
        valorAnterior = 0.0
        operador = ""
        novoNumero = true
    }

    fun digitar(d: String) {
        if (visor.startsWith("Erro")) limpar()
        if (novoNumero) {
            visor = d
            novoNumero = false
        } else if (visor == "0") {
            visor = d
        } else if (visor.length < 15) {
            visor += d
        }
    }

    fun digitarPonto() {
        if (visor.startsWith("Erro")) limpar()
        if (novoNumero) {
            visor = "0."
            novoNumero = false
            return
        }
        var temPonto = false
        for (i in 0 until visor.length) {
            if (visor[i] == '.') temPonto = true
        }
        if (!temPonto) visor += "."
    }

    fun apagar() {
        if (visor.startsWith("Erro")) {
            limpar()
            return
        }
        if (novoNumero) return
        visor = visor.dropLast(1)
        if (visor == "" || visor == "-") visor = "0"
    }

    fun igual() {
        if (operador == "" || visor.startsWith("Erro")) return
        val atual = visor.toDouble()
        if (operador == "÷" && atual == 0.0) {
            visor = "Erro: divisão por zero"
            operador = ""
            novoNumero = true
            return
        }
        val resultado = calcular(valorAnterior, atual, operador)
        if (resultado.isInfinite() || resultado.isNaN()) {
            visor = "Erro: resultado inválido"
        } else {
            visor = formatar(resultado)
        }
        operador = ""
        novoNumero = true
    }

    fun escolherOperador(novo: String) {
        if (visor.startsWith("Erro")) return
        if (operador != "" && !novoNumero) {
            igual()
            if (visor.startsWith("Erro")) return
        }
        valorAnterior = visor.toDouble()
        operador = novo
        novoNumero = true
    }

    Column(
        modifier = Modifier.fillMaxSize().systemBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = if (operador != "") formatar(valorAnterior) + " " + operador else " ",
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
        Text(
            text = visor,
            fontSize = if (visor.length > 12) 28.sp else 48.sp,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.End
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Tecla("C", Modifier.weight(1f).padding(4.dp)) { limpar() }
            Tecla("⌫", Modifier.weight(1f).padding(4.dp)) { apagar() }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Tecla("7", Modifier.weight(1f).padding(4.dp)) { digitar("7") }
            Tecla("8", Modifier.weight(1f).padding(4.dp)) { digitar("8") }
            Tecla("9", Modifier.weight(1f).padding(4.dp)) { digitar("9") }
            Tecla("÷", Modifier.weight(1f).padding(4.dp)) { escolherOperador("÷") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Tecla("4", Modifier.weight(1f).padding(4.dp)) { digitar("4") }
            Tecla("5", Modifier.weight(1f).padding(4.dp)) { digitar("5") }
            Tecla("6", Modifier.weight(1f).padding(4.dp)) { digitar("6") }
            Tecla("×", Modifier.weight(1f).padding(4.dp)) { escolherOperador("×") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Tecla("1", Modifier.weight(1f).padding(4.dp)) { digitar("1") }
            Tecla("2", Modifier.weight(1f).padding(4.dp)) { digitar("2") }
            Tecla("3", Modifier.weight(1f).padding(4.dp)) { digitar("3") }
            Tecla("-", Modifier.weight(1f).padding(4.dp)) { escolherOperador("-") }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Tecla("0", Modifier.weight(1f).padding(4.dp)) { digitar("0") }
            Tecla(".", Modifier.weight(1f).padding(4.dp)) { digitarPonto() }
            Tecla("=", Modifier.weight(1f).padding(4.dp)) { igual() }
            Tecla("+", Modifier.weight(1f).padding(4.dp)) { escolherOperador("+") }
        }
    }
}