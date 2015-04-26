# Pi TurtleCar

使用Android App遙控Raspberry Pi製作的車子，還可以接收從Webcam傳送的影像。

示範影片： 

* 藍牙、燈光加強版：https://youtu.be/MboBbJkQHZE
* MQTT版：http://youtu.be/QvT3QsQqDi8


## 需求
硬體（MQTT版）：

* [Raspberry Pi Model B+](http://www.raspberrypi.org/products/model-b-plus/)或[Raspberry Pi 2 Model B](http://www.raspberrypi.org/products/raspberry-pi-2-model-b/)。
* 8 GB MicroSD 記憶卡。
* 行動電源。
* 無線USB網路卡。[Edimax EW-7811Un](http://www.edimax.com/tw/produce_detail.php?pd_id=301&pl1_id=24&pl2_id=116)。
* L293D，直流馬達控制晶片
* Webcam，[Logitech C270](http://www.logitech.com/zh-tw/product/hd-webcam-c270)
* 直流馬達、6V，兩個
* 齒輪組，兩個
* 輪子，兩個
* 塑膠板
* 膠帶
* Android行動電話，Android 4.0.3或更新的版本。
* 其它需要的零件，例如麵包板、電阻與連接線。

硬體（藍牙、燈光加強版）：

* MQTT版所有零件。
* 高亮度白光LED，前方大燈，兩個
* 黃色LED，兩側方向燈，兩個
* 紅色LED，後方煞車、倒車燈，兩個
* HC-05藍牙模組。

軟體（MQTT版）：

* [Java SE 8 for ARM](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-arm-downloads-2187472.html)。
* [Mosquitto](http://mosquitto.org/)，An Open Source MQTT v3.1/v3.1.1 Broker。
* [Paho](https://eclipse.org/paho/)，MQTT v3 Java用戶端類別庫。

軟體（藍牙版）：

* [Java SE 8 for ARM](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-arm-downloads-2187472.html)。

開發環境：

* Java SE 8
* NetBeans
* Android Studio

## 線路圖

* L293D，直流馬達控制晶片

![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/L293D.png)

* HC-05，藍牙模組

![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/HC05.png)

* 大燈、方向燈與後方煞車、倒車燈

![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/LED.png)

## 安裝Webcam串流模組 - mjpg-streamer

1. 為Raspberry Pi安裝與設定好RASPBIAN作業系統。並確認下列項目：

	* 可以連線到網際網路。
	* 使用ifconfig指令查詢Raspberry Pi的IP位址。
	
2. 登入Raspberry Pi以後，執行下列的指令：

        sudo apt-get install subversion
        sudo apt-get install libjpeg8-dev
        sudo apt-get install imagemagick
        
3. 執行下列的指令建立工作資料夾：

        cd ~
        mkdir webcamwork
        cd webcamwork

4. 執行下載模組的指令：

        svn co https://mjpg-streamer.svn.sourceforge.net/svnroot/mjpg-streamer mjpg-streamer

5. 執行下列的指令製作模組：

        cd mjpg-streamer
        make
        
6. 連接Webcam到Raspberry Pi的USB，執行下列的指令檢查Webcam：

        lsusb

## 安裝MQTT broker - Mosquitto

1. 為Raspberry Pi安裝與設定好RASPBIAN作業系統。並確認下列項目：

	* 可以連線到網際網路。
	* 使用ifconfig指令查詢Raspberry Pi的IP位址。

2. 在工作電腦使用SSH連線到Raspberry Pi。
3. 執行下列的指令安裝Mosquitto（MQTT Broker Server）：

		apt-get install mosquitto

4. 執行下列的指令準備修改Raspberry Pi設定檔：

		sudo nano /etc/hosts

5. 參考下列的內容修改Raspberry Pi的IP位址：

		[Raspberry Pi的IP位址]		RaspberryPi

6. 依序按下「Ctrl+X」、「Enter」與「Y」，儲存檔案與結束nano。
7. 執行下列的指令重新啟動Raspberry Pi：

		sudo reboot

8. Raspberry Pi重新啟動以後，Mosquitto就會開始提供MQTT Broker服務。

## 應用程式專案與開發環境

在「apps」目錄下 __MQTT__ 版的應用程式原始程式碼：

* TurtleCarPi
	* Java嵌入式應用程式
	* 在Raspberry Pi運作
	* 使用Pi4J控制GPIO
	* 使用Paho發佈與接收MQTT訊息
	* NetBeans專案
* TurtleCarMobilePi
	* Android應用程式
	* Android 5、API Level 22
	* 使用Paho發佈與接收MQTT訊息
	* Android Studio專案

在「apps」目錄下 __藍牙__ 版的應用程式原始程式碼：

* TurtleCarPiBlue
	* Java嵌入式應用程式
	* 在Raspberry Pi運作
	* 使用Pi4J控制GPIO
	* 提供藍牙連線與接收控制指令
	* NetBeans專案
* TurtleCarMobilePiBT
	* Android應用程式
	* Android 5、API Level 22
	* 透過藍牙連線到Raspberry Pi後傳送控制指令
	* Android Studio專案

建立Java遠端開發環境：

遠端開發環境安裝與設定請參考[http://www.codedata.com.tw/java/java-embedded-5-dev-env-remote-javase/](http://www.codedata.com.tw/java/java-embedded-5-dev-env-remote-javase/)。

建立Android開發環境：

Android開發環境安裝與設定請參考[http://www.codedata.com.tw/mobile/android-tutorial-the-1st-class-2-android-sdk/](http://www.codedata.com.tw/mobile/android-tutorial-the-1st-class-2-android-sdk/)。

## 準備上場

__MQTT版__ ：

1. 登入Raspberry Pi以後，執行下列的指令啟動「mjpg-streamer」：

        cd ~/webcamwork/mjpg-streamer
        ./mjpg_streamer -i "./input_uvc.so -y  -r QVGA -f 15" -o "./output_http.so -w ./www"

2. 在個人電腦開啟瀏覽器，輸入下列的網址測試「mjpg-streamer」：

        http://Raspberry Pi的IP位址:8080

3. 啟動Raspberry Pi的「TurtleCarPi」應用程式：

        cd 應用程式的位置
        sudo java -jar TurtleCarPi.jar

4. 啟動Android App __TurtleCarMobilePi__ ，選擇右上角的連線圖示：

    ![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/android_screen_01.png)

5. 在「MQTT broker IP」與「Webcam IP」輸入Raspberry Pi的IP位址，選擇連線按鈕：

    ![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/android_screen_02.png)

6. 連線成功後，上方顯示Webcam的即時畫面，下方可以控制車子前進、後退、左轉與右轉：

    ![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/android_screen_03.png)

__藍牙版__ ：

在Raspberry Pi連接HC-05藍牙模組，必須執行下列的設定：

1. 登入Raspberry Pi以後，執行下列的指令，備份原始的設定檔：

        sudo cp /boot/cmdline.txt /boot/cmdline_backup.txt

2. 執行下列的指令，準備修改設定檔：

        sudo nano /boot/cmdline.txt

3. 把檔案修改為下面的內容後存檔：

        dwc_otg.lpm_enable=0 rpitestmode=1 console=tty1 root=/dev/mmcblk0p2 rootfstype=ext4 rootwait

4. 執行下列的指令，準備修改設定檔：

        sudo nano /etc/inittab

5. 找到下面的內容，再這一行的最前面加入「#」：

        #T0:23:respawn:/sbin/getty -L ttyAMA0 115200 vt100

6. 執行下面關機的指令：

        sudo poweroff

7. 依照上面的線路圖為Raspberry Pi連接HC-05藍牙模組。

啟動與使用應用程式：

1. 登入Raspberry Pi以後，執行下列的指令啟動「mjpg-streamer」：

        cd ~/webcamwork/mjpg-streamer
        ./mjpg_streamer -i "./input_uvc.so -y  -r QVGA -f 15" -o "./output_http.so -w ./www"

2. 在個人電腦開啟瀏覽器，輸入下列的網址測試「mjpg-streamer」：

        http://Raspberry Pi的IP位址:8080

3. 啟動Raspberry Pi的「TurtleCarPiBlue」應用程式：

        cd 應用程式的位置
        sudo java -jar TurtleCarPiBlue.jar

4. 啟動Android App __TurtleCarMobilePiBT__ ，選擇右上角的藍牙連線圖示：

    ![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/android_screen_04.png)

5. 選擇「Scan」掃描Raspberry Pi的HC-05藍牙模組，配對以後選擇連線：

    ![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/android_screen_05.png)

6. 連線成功後，上方顯示Webcam的即時畫面，下方左側可以控制車子前進、後退、左轉與右轉，下方右側可以開啟與關閉大燈：

    ![](https://github.com/macdidi5/PiTurtleCar/blob/master/images/android_screen_06.png)



