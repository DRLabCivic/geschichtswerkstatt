# geschichtswerkstatt

Android app für die geschichtswerkstatt platform brandis

## Setup IDE

* Create a file keys.xml within res/values Folder:

  ```
  <?xml version="1.0" encoding="utf-8"?>
  <resources>
      <string name="places_api_key">{google places api string}</string>
  </resources>
  ```



## QR Codes für App erstellen:

QR Code Strings müssen mit dem Prefix `gwb://` starten um erkannt zu werden. Danach folgt der pfad zur geschichte ausgehend von der domain `http://geschichtswerkstatt.brandis.eu/` 

Der QR String `gpb://am-weihnachtsmarkt/` verlinkt also zu `http://geschichtswerkstatt.brandis.eu/am-weihnachtsmarkt/`.