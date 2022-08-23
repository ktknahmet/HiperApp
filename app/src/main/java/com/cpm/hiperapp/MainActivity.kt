package com.cpm.hiperapp

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import com.cpm.hiperapp.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var images = ArrayList<Int>() //tavşan tilki uçak gibi var olan resimleri listeye eklemek için tanımlama yaptık
    private var basarili=0 //başarılı tıklanmayı yakalamak için tanım yaptık ve başlangıçta 0 verdik
    private var basarisiz=0 //başarısız tıklanmayı yakalamak için tanım yaptık ve başlangıçta 0 verdik
    private var kalanSure:Long = 600000 //kalan süreyi hesaplamak için yazdık 600000 milisayniye 10 dakika yapar
    private var handler= Handler()
    private var runnable = Runnable {  } //her geçen sürede ne olacağını tanımladık
    private var ses: MediaPlayer? =null //seslerin çalışması için tanımlama yaptık

    //tanımlamaları yaptık
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //başla butonuna tıklanınca ne olacak
        binding.basla.setOnClickListener {
                images() //ilk başta bu fonksiyon çalışır
                showImages() // sonra bu fonksiyon
                timer() //en son burdaki fonksiyon çalışır
            }

    }


    private fun timer(){
        object: CountDownTimer(600000,1000){ //600000 milisaniyede başlayacak yani 10 dakika ve 1000 milisaniye yani 1 saniye azalacak 600-599-598 gibi

            override fun onTick(millisUntilFinished: Long) {
                kalanSure=millisUntilFinished
                println("Başarısız :$basarisiz")
                //onTick ile her saniye ne olacak onu belirleriz ve kalanSure her saniye azalır
            }

            override fun onFinish() {
                //süre bittiği zaman handler kapatırız
                println("Başarısız Bitti :$basarisiz")
                handler.removeCallbacks(runnable)

                val intent = Intent(this@MainActivity,RaporActivity::class.java) //süre bittiğinde rapor sayfasına geçişi yaparız
                intent.putExtra("Basarisiz",basarisiz)
                startActivity(intent)
                finish()
            }
        }.start()
    }
    private fun images(){
        //üstte oluşturulan listeye resimleri ekleriz
        images.add(R.drawable.araba)
        images.add(R.drawable.tilki)
        images.add(R.drawable.tavsan)
        images.add(R.drawable.ucak)
        images.add(R.drawable.sincap)

    }

    private fun showImages(){
        val random= Random()
        runnable = Runnable {

            //test başladığı zaman başla butonunu gizleriz
            binding.basla.visibility=View.INVISIBLE

            val randomSayi = random.nextInt(5) //0-5 arasında random sayı oluştururuz ve bu her saniye değişir


            binding.imageView.setImageResource(images[randomSayi]) //oluşturulan random sayının yerine göre resimler gelir
            // örnek: random sayı=0 diyelim listedeki 0.indexte araba simgesi var ve ekranda araba simgesi gözükür ve her saniye bu değişir
            if(kalanSure<420000){
                //eğer kalan süre 7.dakikadan az ise hızını 2 saniye yap
                handler.postDelayed(runnable,2000)
            }else{
                //eğer kalan süre 7.dakikadan az değil ise hızını 3 saniye yap
                handler.postDelayed(runnable,3000)
            }

            if(kalanSure in 240001..360001){
                //test 4.dakika ile 6.dakika arasında ise hangi resim varsa ona ait sesi çal
                //çıkan görsel araba ise araba sesi çal uçağa geçtiğinde arabayı durdur uçağı çal
                when (binding.imageView.drawable.constantState) {
                    ContextCompat.getDrawable(this,R.drawable.araba)?.constantState -> {
                        ses?.release()
                        ses=null
                        ses=MediaPlayer.create(this,R.raw.araba_sesi)
                        ses?.start()
                    }
                    ContextCompat.getDrawable(this,R.drawable.ucak)?.constantState -> {
                        ses?.release()
                        ses=null
                        ses=MediaPlayer.create(this,R.raw.ucak_sesi)
                        ses?.start()
                    }
                    ContextCompat.getDrawable(this,R.drawable.tavsan)?.constantState -> {
                        ses?.release()
                        ses=null
                        ses = MediaPlayer.create(this,R.raw.kus_sesi)
                        ses?.start()
                    }
                    ContextCompat.getDrawable(this,R.drawable.tilki)?.constantState -> {
                        ses?.release()
                        ses=null
                    }
                    ContextCompat.getDrawable(this,R.drawable.sincap)?.constantState -> {
                        ses?.release()
                        ses=null
                    }
                }
            }
            //yakala butonuna basıldığında ekranda tavşan görseli yoksa başarısız değişkenini arttır
            //tavşan görseli varsa başarılı değişkenini arttır
            binding.yakala.setOnClickListener {
                if(binding.imageView.drawable.constantState != ContextCompat.getDrawable(this, R.drawable.tavsan)?.constantState
                ){
                    basarisiz++
                    println("Başarısız :$basarisiz")
                }else{
                    basarili++
                }
            }
        }
        //tüm bunların olması için handler.post ile oluşturduğumuz runnable çalıştırırız
        handler.post(runnable)
    }

}