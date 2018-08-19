# Building Run

Air quality and heart data is measured through sensors. A user can view overall data in both app and web.
(Related to IoT Development Program, 2018 summer, UCSD.)


## Getting Started

### Vagrant

First, install vagrant to set the local server.
```
sudo apt-get install virtualbox
sudo apt-get install vagrant
```

Go to where the VagrantFile is and type the following.
```
vagrant init
vagrant up
```

Now the server is started. To end the server, type the following.

```
vagrant halt
```

### Android Studio

Install Android Studio to install apk file in you phone or virtual machine.
You can download it at https://developer.android.com/studio/

## Built With

* [PHP](http://php.net/) - The web framework used
* [MySQL](https://www.mysql.com/) - Database management
* [Android Studio](https://developer.android.com/studio/) - The android framework used
* [Udoo board](https://www.udoo.org/) - Air quality measurement

## Contributors

* **Joowon Byun** - *Web development* - https://github.com/Joowon0
* **Umji Choi (Wendy)** - *Web development* - https://github.com/cute2969
* **Doyoung Ha (James)** - *App development* - https://github.com/gkehdud
* **Janghyun Song (Jack)** - *App development* - https://github.com/jhsong13
* **Junho Suk (Juno)** - *Sensor development* - https://github.com/aaatype
