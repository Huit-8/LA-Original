package app.aoyama.huit.original

import android.app.ActionBar
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.CalendarContract
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import app.aoyama.huit.original.databinding.ActivityProjectBinding
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ProjectActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityProjectBinding

    //Databaseの変数を用意
    lateinit var db: AppDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //データベースの初期化
        db = AppDatabase.getInstance(this.applicationContext)!!

        //SharedPrefの定義
        val pref: SharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)

        val activeHour1 = pref.getString("hour1", "NoData")
        val activeMinute1 = pref.getString("minute1", "NoData")
        val activeHour2 = pref.getString("hour2", "NoData")
        val activeMinute2 = pref.getString("minute2", "NoData")



        //toolbarの設定をする
        binding.toolBar.setTitle("Projects")
        setSupportActionBar(binding.toolBar)
        //toolbarに戻るボタンをセットする
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //HomeFragmentからデータを取得する
        val tap = intent.getIntExtra("tap", 0)

        //MainActivityに遷移する準備をする
        val homeIntent: Intent = Intent(this, MainActivity::class.java)

        //calculateButtonが押されたときの処理を書く
        binding.calculateButton.setOnClickListener {
            val dueDateText = binding.dueDateText.text
            if (dueDateText == "") {
                //日付が選択されていないときは計算しない
                Toast.makeText(applicationContext, "日付を選択してください", Toast.LENGTH_SHORT).show()
            } else {
                //日付が選択されているときは、計算するメソッドを呼び出す
                //カレンダー一覧を取得する
                searchCalendarId()
                getCalendarEventAll(6)
            }
        }

        if (tap == 1) {
            //既存のデータを表示する処理を書く
            val projectName = intent.getStringExtra("name")
            val projectDueDate = intent.getStringExtra("dueDate")
            val projectRemain = intent.getStringExtra("remainingTime")
            val projectId = intent.getIntExtra("id", 0)
            binding.projectNameEditText.setText(projectName)
            binding.dueDateText.text = projectDueDate
            binding.remainingTimeText.text = projectRemain

            //TODO 保存されているremainingTimeを表示する処理を書く


            //saveButtonが押されたときにデータの更新をする
            binding.saveButton.setOnClickListener {
                val editedProjectName = binding.projectNameEditText.text.toString()
                val editedProjectDueDate = binding.dueDateText.text.toString()
                val editedRemainingTime = binding.remainingTimeText.text.toString()

                val project: Project = Project(
                    name = editedProjectName,
                    due = editedProjectDueDate,
                    //TODO remainingTimeをroomに保存する処理を追加する
                    remain = editedRemainingTime,
                    pid = projectId
                )
                db.projectDao().update(project)

            }
        } else {
            //新規プロジェクトを作成するコードを書く
            //saveButtonが押された時の処理を書く
            binding.saveButton.setOnClickListener {

                val projectName = binding.projectNameEditText.text.toString()
                val dueDate = binding.dueDateText.text.toString()
                val remainingTime = binding.remainingTimeText.text.toString()

                if (projectName == "") {
                    Toast.makeText(applicationContext, "入力されていません", Toast.LENGTH_SHORT).show()
                } else {
                    //プロジェクトの名前と期限をデータベースに保存する
                    val project: Project = Project(
                        name = projectName,
                        due = dueDate,
                        remain = remainingTime,
                        //TODO remainingTImeをroomに保存する処理を追加する
                    )
                    db.projectDao().insert(project)
                    startActivity(homeIntent)
                }
            }
        }

        //戻るボタンが押された時の処理を書く
        val callback = this.onBackPressedDispatcher.addCallback(this) {
            startActivity(homeIntent)
        }
    }

    //カレンダーId一覧を取り出すメソッドを書く
    fun searchCalendarId() {
        //取得したい情報をListにまとめて入れる
        val CALENDAR_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
        )

        //CALENDAR_PROJECTION配列の要素順で番号を決める
        val CALENDAR_PROJECTION_IDX_ID = 0
        val CALENDAR_PROJECTION_IDX_NAME = 1
        val CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT = 2

        // クエリ条件を設定する
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection: String? = null
        val selectionArgs: Array<String>? = null
        val sortOrder: String? = null

        // クエリを発行してカーソルを取得する
        val cr = contentResolver
        val cur = cr.query(uri, CALENDAR_PROJECTION, selection, selectionArgs, sortOrder)

        var textStr = "id, カレンダー名, ownerAccount\n"
        while (cur?.moveToNext() == true) {
            // カーソルから各プロパティを取得する
            val id = cur.getLong(CALENDAR_PROJECTION_IDX_ID)
            val name = cur.getString(CALENDAR_PROJECTION_IDX_NAME)
            val ownerAccount = cur.getString(CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT)
            textStr += "$id, "
            textStr += "$name, "
            textStr += "$ownerAccount\n"

//            println("id $id")
//            println("name $name")
//            println("ownerAccount $ownerAccount")
        }
    }

    //指定したカレンダーIDのイベントを取得するメソッドをつくる
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCalendarEventAll(targetCalendarId: Int) {
        val EVENT_PROJECTION = arrayOf(
            BaseColumns._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.EVENT_COLOR,
            CalendarContract.Events.DISPLAY_COLOR,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_END_TIMEZONE,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.GUESTS_CAN_MODIFY,
            CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
            CalendarContract.Events.GUESTS_CAN_SEE_GUESTS,
            CalendarContract.Events.ORGANIZER,
            CalendarContract.Events.CALENDAR_ID,
        )

        // EVENT_PROJECTIONの順番と一致するID、要素数書き換えたり、順番変えたときには、ここも変える
        val EVENT_PROJECTION_IDX_EVENT_ID = 0
        val EVENT_PROJECTION_IDX_TITLE = 1
        val EVENT_PROJECTION_IDX_DESCRIPTION = 2
        val EVENT_PROJECTION_IDX_EVENT_LOCATION = 3
        val EVENT_PROJECTION_IDX_EVENT_COLOR = 4
        val EVENT_PROJECTION_IDX_DISPLAY_COLOR = 5
        val EVENT_PROJECTION_IDX_DTSTART = 6
        val EVENT_PROJECTION_IDX_DTEND = 7
        val EVENT_PROJECTION_IDX_DURATION = 8
        val EVENT_PROJECTION_IDX_EVENT_TIMEZONE = 9
        val EVENT_PROJECTION_IDX_EVENT_END_TIMEZONE = 10
        val EVENT_PROJECTION_IDX_ALL_DAY = 11
        val EVENT_PROJECTION_IDX_RRULE = 12
        val EVENT_PROJECTION_IDX_RDATE = 13
        val EVENT_PROJECTION_IDX_GUESTS_CAN_MODIFY = 14
        val EVENT_PROJECTION_IDX_GUESTS_CAN_INVITE_OTHERS = 15
        val EVENT_PROJECTION_IDX_GUESTS_CAN_SEE_GUESTS = 16
        val EVENT_PROJECTION_IDX_ORGANIZER = 17
        val EVENT_PROJECTION_IDX_CALENDAR_ID = 18

        val uri = CalendarContract.Events.CONTENT_URI
        // クエリ
        val selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)"
        val selectionArgs = arrayOf(targetCalendarId.toString())

        val cr = contentResolver
        val cur: Cursor? = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null)

        var textStr = "eventId, イベントタイトル,開始時間,終了時間,開始時刻,終了時刻,期間,詳細\n"

        //各イベントの期間を並べるリストを作る
        val durationList: MutableList<Float> = mutableListOf()

        //イベント期間の合計を入れる変数を定義する
        var durationSum = 0f

        //SharedPrefの定義
        val pref: SharedPreferences = getSharedPreferences("SharedPref", Context.MODE_PRIVATE)

        val activeHour1 = pref.getString("hour1", "NoData")
        val activeMinute1 = pref.getString("minute1", "NoData")
        val activeHour2 = pref.getString("hour2", "NoData")
        val activeMinute2 = pref.getString("minute2", "NoData")

        //イベントのカウントする期間を決めるための、アクティブタイムを定義する
        val activeHourFloat1 = activeHour1?.toFloat()
        val preActiveMinuteFloat1 = activeMinute1?.toFloat()
        val activeHourFloat2 = activeHour2?.toFloat()
        val preActiveMinuteFloat2 = activeMinute2?.toFloat()

        var activeMinuteFloat1 = 0.0f
        var activeMinuteFloat2 = 0.0f

        //7:30とかの30を0.5で表す
        if (preActiveMinuteFloat1 != null) {
            if (preActiveMinuteFloat1 >= 30) {
                activeMinuteFloat1 = 0.5f
            }
        }
        if (preActiveMinuteFloat2 != null) {
            if (preActiveMinuteFloat2 >= 30) {
                activeMinuteFloat2 = 0.5f
            }
        }

        //ActiveTimeを定義する
        val activeStart = activeHourFloat1?.plus(activeMinuteFloat1)
        val activeEnd = activeHourFloat2?.plus(activeMinuteFloat2)

        //dueDateを取得する
        val dueDateText = binding.dueDateText.text

        //日付の文字列の形を定義する
        val dueDateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

        //dueDateをLocalDate型に変換する
        val localDueDate = LocalDate.parse(dueDateText,dueDateFormatter)

        //LocalTimeをZoneTimeに変換する
        val zoneDueDate = localDueDate.atStartOfDay(ZoneOffset.ofHours(+9))

        //zoneTimeをunixTimeに変換する（ミリ秒）
        val unixDueDate = zoneDueDate.toEpochSecond() * 1000

        //現在のunixTimeを取得する
        val unixTime: Long = System.currentTimeMillis()

        //dueDateから現在を引いて、期限までの総残り時間を定義する
        val totalRemain = unixDueDate - unixTime

        //ActiveTime外の時間をtotalHourRemainから引く処理を書く
        val activeDuration = activeEnd?.minus(activeStart!!)

        //dueDateまでの残り時間からActiveTime＊日にち分の時間を引く
        //残り何日かを求める
        val remainDates = (totalRemain/86400000).toInt()
        val totalActiveTime = activeDuration?.times(remainDates)


        while (cur?.moveToNext() == true) {
            // カーソルから各プロパティを取得する
            val eventId: Long = cur.getLong(EVENT_PROJECTION_IDX_EVENT_ID)
            val title: String? = cur.getString(EVENT_PROJECTION_IDX_TITLE)
            val description: String? = cur.getString(EVENT_PROJECTION_IDX_DESCRIPTION)
            val eventLocation: String? = cur.getString(EVENT_PROJECTION_IDX_EVENT_LOCATION)
            val eventColdr: Int = cur.getInt(EVENT_PROJECTION_IDX_EVENT_COLOR)
            val displayColor: Int = cur.getInt(EVENT_PROJECTION_IDX_DISPLAY_COLOR)
            val dtStart: Long = cur.getLong(EVENT_PROJECTION_IDX_DTSTART)
            val dtEnd: Long = cur.getLong(EVENT_PROJECTION_IDX_DTEND)
            val duration: String? = cur.getString(EVENT_PROJECTION_IDX_DURATION)
            val eventTimeZone: String? = cur.getString(EVENT_PROJECTION_IDX_EVENT_TIMEZONE)
            val eventEndTimeZone: String? = cur.getString(EVENT_PROJECTION_IDX_EVENT_END_TIMEZONE)
            val allDay: Int = cur.getInt(EVENT_PROJECTION_IDX_ALL_DAY)
            val rRule: String? = cur.getString(EVENT_PROJECTION_IDX_RRULE)
            val rDate: String? = cur.getString(EVENT_PROJECTION_IDX_RDATE)
            val guestsCanModify: Int = cur.getInt(EVENT_PROJECTION_IDX_GUESTS_CAN_MODIFY)
            val guestsCanInviteOthers: Int =
                cur.getInt(EVENT_PROJECTION_IDX_GUESTS_CAN_INVITE_OTHERS)
            val guestCanSeeGuests: Int = cur.getInt(EVENT_PROJECTION_IDX_GUESTS_CAN_SEE_GUESTS)
            val organizer: String? = cur.getString(EVENT_PROJECTION_IDX_ORGANIZER)
            val calenderId: Long = cur.getLong(EVENT_PROJECTION_IDX_CALENDAR_ID)

            //イベント何時から始まるか計算する処理を書く
            val eventStartSecond = dtStart / 1000
            val eventStartFrom0Second = (eventStartSecond + 32400) % 86400
            val eventStartHourLong = eventStartFrom0Second / 3600
            val eventStartHour = eventStartHourLong.toFloat()
            val eventStartSub = eventStartFrom0Second.toInt()
            var eventStartMinute = 0.0f
            if (eventStartSub % 3600 != 0) {
                eventStartMinute = 0.5f
            }

            var eventStart = eventStartHour + eventStartMinute

            //イベントが何時に終わるかを計算する処理を書く
            val eventEndSecond = dtEnd / 1000
            val eventEndFrom0Second = (eventEndSecond + 32400) % 86400
            val eventEndHourLong = eventEndFrom0Second / 3600
            val eventEndHour = eventEndHourLong.toFloat()
            val eventEndSub = eventEndFrom0Second.toInt()
            var eventEndMinute = 0.0f
            if (eventEndSub % 3600 != 0) {
                eventEndMinute = 0.5f
            }

            var eventEnd = eventEndHour + eventEndMinute

            //ActiveTimeの範囲にかかる部分の時間だけ抽出する処理を書く
            //ESがASより早い
            if (eventStart < activeStart!!) {
                if (eventEnd < activeStart) {
                    //カウントしない
                    eventStart = 0f
                    eventEnd = 0f

                } else {
                    if (eventEnd <= activeEnd!!) {
                        //イベントの前半差分を切り取る処理を書く
                        eventStart = activeStart


                    } else {
                        //イベントの前後半差分を切り取る処理を書く
                        eventStart = activeStart
                        eventEnd = activeEnd
                    }
                }


            } else {
                //ESがASより遅い
                if (eventStart > activeEnd!!) {
                    //カウントしない
                    eventStart = 0f
                    eventEnd = 0f
                } else {
                    if (eventEnd <= activeEnd) {
                        //全ての時間カウントする

                    } else {
                        //後半部分を切り取る処理を書く
                        eventEnd = activeEnd
                    }
                }
            }

            //ActiveTimeにおけるイベントのdurationを定義する
            val eventDuration = eventEnd - eventStart

            // タイトルが無いものは、表示しない
            if (title.isNullOrEmpty().not()) {
                //今日以降のデータを表示する
                if (dtStart >= unixTime) {
                    //期限までのイベントを表示する処理を書く（DueDateとdtEndの比較）
                    if (dtEnd<=unixDueDate){
                        textStr += "$eventId, "
                        textStr += "$title, "
                        textStr += "$dtStart,"
                        textStr += "$dtEnd,"
                        textStr += "$description\n"

                        durationList += eventDuration
                        durationSum = durationList.sum()

                        println(title + eventDuration)
                    }
                }
            }
        }
        //DueDateまでの合計時間からdurationSumを引き、remainingTimeとして定義する
        val remainingTime = totalActiveTime?.minus(durationSum)
        //remainingTimeをtextViewに表示する処理を書く
        binding.remainingTimeText.text = remainingTime.toString()

        println(durationSum)
        println(totalActiveTime)
    }

    //DatePickerの処理を書く
    override fun onDateSet(p0: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val dueDate: String = getString(R.string.string_format, year, monthOfYear + 1, dayOfMonth)
        binding.dueDateText.text = dueDate
    }

    //datePickIconが押された時に呼び出される関数を作る
    fun showDatePickerDialog(v: View) {
        val newFragment = DatePick()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    //menuにあるproject用のtoolbarをinflateする
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        binding.toolBar.inflateMenu(R.menu.projects_top_app_bar)
        return true
    }

    //toolbarのクリック処理を書く
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mainIntent = Intent(this, MainActivity::class.java)
        if (item.itemId == android.R.id.home) {
            //戻るボタンが押されたときの処理を書く
            startActivity(mainIntent)
            println("あいうえお")
        } else {
            //ゴミ箱ボタンが押された時の処理を書く
            val tap = intent.getIntExtra("tap", 0)
            if (tap == 1) {
                //既存のデータがタップされているときの処理
                val projectName = binding.projectNameEditText.text.toString()
                val projectDueDate = binding.dueDateText.text.toString()
                val projectRemainingTime = binding.remainingTimeText.toString()
                val projectId = intent.getIntExtra("id", 0)
                val project: Project = Project(
                    name = projectName,
                    due = projectDueDate,
                    //TODO projectRemainingTImeをroomに保存する処理を書く
                    remain = projectRemainingTime,
                    pid = projectId,
                )
                db.projectDao().delete(project)
                startActivity(mainIntent)
            } else {
                //新規データが作成される画面のときの処理
                val emptyText = ""
                binding.dueDateText.text = ""
                binding.projectNameEditText.setText(emptyText)
                binding.remainingTimeText.text = ""
            }
        }
        return true
    }

}