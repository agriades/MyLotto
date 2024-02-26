package com.android.mylotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    private val clearButton by lazy { findViewById<Button>(R.id.btn_clear) }
    private val addButton by lazy { findViewById<Button>(R.id.btn_add) }
    private val runButton by lazy { findViewById<Button> (R.id.btn_run) }
    private val numPick by lazy { findViewById<NumberPicker> (R.id.np_num) }
    //왜 첫번째 lazy는 이렇게 타입을 바로 옆에 적어주어야 빨간 줄이 안 뜨지? 재실행하니 사라졌네.

    //list에 6개의 공을 만들어 꺼내기
    private val numTextViewList : List<TextView> by lazy {
        listOf<TextView>(
            findViewById(R.id.tv_num1),
            findViewById(R.id.tv_num2),
            findViewById(R.id.tv_num3),
            findViewById(R.id.tv_num4),
            findViewById(R.id.tv_num5),
            findViewById(R.id.tv_num6)
        )
    }

    //지금 run이 실행 중인지 체크하는 변수
    private var didRun = false //true면 번호 추가 불가
    private var pickNumberSet = hashSetOf<Int>()


    //여기까지는 밑작업. onCreate부터 본격적 앱 실행...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numPick.minValue = 1
        numPick.maxValue = 45

        initAddButton()
        initRunButton()
        initClearButton()
    }

    private fun initAddButton() { //최대 5개 '직접' 픽
        addButton.setOnClickListener{
            when {
                didRun -> showToast("초기화 후에 시도해주세요.")
                pickNumberSet.size >= 5 -> showToast("숫자는 최대 5개까지 선택할 수 있습니다.")
                pickNumberSet.contains(numPick.value) -> showToast("이미 선택된 숫자입니다.")//중복방지
                else -> {
                    val textView = numTextViewList[pickNumberSet.size]
                    textView.isVisible = true
                    textView.text = numPick.value.toString()

                    setNumBack(numPick.value, textView)
                    pickNumberSet.add(numPick.value)
                }

            }
        }
    }
    private fun initRunButton() { //자동 생성 시작
        runButton.setOnClickListener {
            val list = getRandom()
            didRun = true //6개 다 뽑았으니까.
            list.forEachIndexed { index, number ->
                val textView = numTextViewList[index]
                textView.text = number.toString() //공에 뽑은 번호 써 주세요!
                textView.isVisible = true //공 까 주세요.
                setNumBack(number, textView)
            }
        }
    }

    private fun getRandom(): List<Int> { //숫자 6개 리스트 반환
        val numbers = (1 .. 45).filter { it !in pickNumberSet} //이미 뽑은 숫자 제외, 1~45
        return (pickNumberSet + numbers.shuffled().take(6-pickNumberSet.size)).sorted()
        //엥 로또는 순서 상관 없나?
        //이미 뽑은 숫자 리스트 + 1~45 중 안 뽑은 숫자들.섞어서.남은 만큼 뽑아서.오름차순! 정렬
    }

    private fun initClearButton() { //초기화
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numTextViewList.forEach{it.isVisible = false} //다시 다 안 보이게 꺼줘요!
            didRun = false
            numPick.value = 1
        }
    }

    private fun setNumBack(number: Int, textView: TextView){
        val background = when(number) { //number별 색상 지정
            in 1 .. 10 -> R.drawable.circle_yellow
            in 11 .. 20 -> R.drawable.circle_blue
            in 21 .. 30 -> R.drawable.circle_red
            in 31 .. 40 -> R.drawable.circle_grey
            else -> R.drawable.circle_green
        }
        textView.background = ContextCompat.getDrawable(this, background)
        //위에 R.drawable이 있는데 또 이걸 써야 하나봐.
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}