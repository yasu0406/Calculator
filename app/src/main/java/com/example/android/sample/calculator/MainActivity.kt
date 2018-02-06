package com.example.android.sample.calculator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

class MainActivity : AppCompatActivity(), TextWatcher, View.OnClickListener {

    // 上のEditText
    private var numberInput1: EditText? = null
    // 下のEditText
    private var numberInput2: EditText? = null
    // 演算子選択用のSpinner
    private var operatorSelector: Spinner? = null
    // 計算結果のTextView
    private var calcResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 上のEditText
        numberInput1 = findViewById<View>(R.id.numberInput1) as EditText
        // 上のEditTextの文字入力イベントを受け取る
        numberInput1!!.addTextChangedListener(this)

        // 下のEditText
        numberInput2 = findViewById<View>(R.id.numberInput2) as EditText
        // 下のEditTextの文字入力イベントを受け取る
        numberInput2!!.addTextChangedListener(this)

        // 演算子選択用のSpinner
        operatorSelector = findViewById<View>(R.id.operatorSelector) as Spinner

        // 計算結果のTextView
        calcResult = findViewById<View>(R.id.calcResult) as TextView

        // 上の「計算」ボタン
        findViewById<View>(R.id.calcButton1).setOnClickListener(this)
        // 下の「計算」ボタン
        findViewById<View>(R.id.calcButton2).setOnClickListener(this)
        // 「続けて計算する」ボタン
        findViewById<View>(R.id.nextButton).setOnClickListener(this)
    }

    // 2つのEditTextに入力がされているかをチェックする 
    private fun checkEditTextInput(): Boolean {
        // 入力内容を取得する 
        val input1 = numberInput1!!.text.toString()
        val input2 = numberInput2!!.text.toString()
        // 2つとも「空 or null」でなければ、true 
        return !TextUtils.isEmpty(input1) && !TextUtils.isEmpty(input2)
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // テキストが変更される直前に呼ばれる。sは変更前の内容
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // テキストが変更される時に呼ばれる。sは変更後の内容で編集不可。
    }

    override fun afterTextChanged(s: Editable) {
        // テキストが変更された後に呼ばれる。sは変更後の内容で編集可能。
        // 必要があれば計算を行い、結果を表示する
        refreshResult()
    }

    // 計算結果の表示を更新する 
    private fun refreshResult() {
        if (checkEditTextInput()) {
            // 計算を行う
            val result = calc()

            // 計算結果用のTextViewを書き換える 
            val resultText = getString(R.string.calc_result_text, result)
            calcResult!!.text = resultText
        } else {
            // どちらかが入力されていない状態の場合、計算結果用の表示をデフォルトに戻す
            calcResult!!.setText(R.string.calc_result_default)
        }
    }

    // 計算を行う
    private fun calc(): Int {
        // 入力内容を取得する
        val input1 = numberInput1!!.text.toString()
        val input2 = numberInput2!!.text.toString()

        // int型に変換する
        val number1 = Integer.parseInt(input1)
        val number2 = Integer.parseInt(input2)

        // Spinnerから、選択中のindexを取得する
        val operator = operatorSelector!!.selectedItemPosition

        // indexに応じて計算結果を返す。
        when (operator) {
            0 // 足し算
            -> return number1 + number2
            1 // 引き算
            -> return number1 - number2
            2 // 掛け算
            -> return number1 * number2
            3 // 割り算
            -> return number1 / number2
            else ->
                // 通常発生しない
                throw RuntimeException()
        }
    }

    override fun onClick(v: View) {
        // タップされたViewのIDを取得する。
        val id = v.id
        // IDごとに違う処理を行う 
        when (id) {
            R.id.calcButton1 -> {
                // 上の「計算」ボタンが押された時の処理 
                val intent1 = Intent(this, AnotherCalcActivity::class.java)
                startActivityForResult(intent1, REQUEST_CODE_ANOTHER_CALC_1)
            }
            R.id.calcButton2 -> {
                // 下の「計算」ボタンが押された時の処理 
                val intent2 = Intent(this, AnotherCalcActivity::class.java)
                startActivityForResult(intent2, REQUEST_CODE_ANOTHER_CALC_2)
            }
            R.id.nextButton ->
                // 「続けて計算する」ボタンが押された時の処理
                // 両方のEditTextに値が設定されていれば、処理を行う
                if (checkEditTextInput()) {
                    // 計算する
                    val result = calc()
                    // 上のEditTextの値を書き換える
                    numberInput1!!.setText(result.toString())
                    // 計算し直して、画面を更新する
                    refreshResult()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // 結果が「OK」ではない場合は、何もしない 
        if (resultCode != Activity.RESULT_OK) return

        // 結果データセットを取り出す 
        val resultBundle = data.extras

        // 結果データセットに、所定のキーが含まれていない場合、何もしない 
        if (!resultBundle!!.containsKey("result")) return

        // 結果データから、"result"キーに対応するint値を取り出す 
        val result = resultBundle.getInt("result")

        if (requestCode == REQUEST_CODE_ANOTHER_CALC_1) {
            // 上の「計算」ボタンを押した後、戻ってきた場合
            numberInput1!!.setText(result.toString())
        } else if (requestCode == REQUEST_CODE_ANOTHER_CALC_2) {
            // 上の「計算」ボタンを押した後、戻ってきた場合
            numberInput2!!.setText(result.toString())
        }

        // 計算をし直して、結果を表示する
        refreshResult()
    }

    companion object {
        // 上の「計算ボタン」を押した時のリクエストコード 
        private val REQUEST_CODE_ANOTHER_CALC_1 = 1
        // 上の「計算ボタン」を押した時のリクエストコード 
        private val REQUEST_CODE_ANOTHER_CALC_2 = 2
    }
}
