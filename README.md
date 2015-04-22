# Pi TurtleCar

使用Android App遙控Raspberry Pi製作的車子，還可以接收從Webcam傳送的影像。

示範影片：

* 2015/0x/xx：https://

## 歷程

* 2015/03/18。開放原始程式碼，請參考[這裡](https://github.com/macdidi5/PiCommander/blob/master/ProjectNotes.md)的說明。

## 需求
硬體：

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

軟體：

* [Java SE 8 for ARM](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-arm-downloads-2187472.html)。
* [Mosquitto](http://mosquitto.org/)，An Open Source MQTT v3.1/v3.1.1 Broker。
* [Paho](https://eclipse.org/paho/)，MQTT v3 Java用戶端類別庫。

開發環境：

* Java SE 8
* NetBeans
* Android Studio

## 線路圖

* L293D，直流馬達控制晶片

![](https://github.com/macdidi5/ARMmbedTurtleCar/blob/master/images/L293D.png)

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

## 準備上場了！

1. 登入Raspberry Pi以後，執行下列的指令啟動「mjpg-streamer」：

        cd ~/webcamwork/mjpg-streamer
        ./mjpg_streamer -i "./input_uvc.so -y  -r QVGA -f 15" -o "./output_http.so -w ./www"

2. 在個人電腦開啟瀏覽器，輸入下列的網址測試「mjpg-streamer」：

        http://Raspberry Pi的IP位址:8080

3. 啟動Raspberry Pi的「TurtleCarPi」應用程式：

        cd 應用程式的位置
        sudo java -jar TurtleCarPi.jar

4. 啟動Android App，選擇右上角的連線圖示：

    ![](https://github.com/macdidi5/ARMmbedTurtleCar/blob/master/images/L293D.png)

5. 在「MQTT broker」與「」輸入Raspberry Pi的IP位址，選擇連線按鈕：

    ![](https://github.com/macdidi5/ARMmbedTurtleCar/blob/master/images/L293D.png)

6. 連線成功後，上方顯示Webcam的即時畫面，下方可以控制車子前進、後退、左轉與右轉：

    ![](https://github.com/macdidi5/ARMmbedTurtleCar/blob/master/images/L293D.png)

## 後記

這個專案使用MQTT技術，讓Android App控制Raspberry Pi，比較不是MQTT適合的應用。因為找不到USB藍牙給Raspberry Pi使用，透過藍牙搖控車子會比較好一些，或許以後會把這個專案改為藍牙搖控車。



