<img width="82" align="left" src="https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/src/assets/icon.svg"/>

# TinyTotp

A Totp Client

## Downloads

java version | app version | jar | deb
:----------: | :---------: | :-: | :-:
18+          | 4.4.1       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.4.1/TinyTotp.jar) | [TinyTotp-4.4.1.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.4.1/TinyTotp-4.4.1.deb)
18+          | 4.4.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.4.0/TinyTotp.jar) | [TinyTotp-4.4.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.4.0/TinyTotp-4.4.0.deb)
18+          | 4.3.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.3.0/TinyTotp.jar) | [TinyTotp-4.3.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.3.0/TinyTotp-4.3.0.deb)
18+          | 4.2.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.2.0/TinyTotp.jar) | [TinyTotp-4.2.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.2.0/TinyTotp-4.2.0.deb)
18+          | 4.1.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.1.0/TinyTotp.jar) | [TinyTotp-4.1.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.1.0/TinyTotp-4.1.0.deb)
18+          | 4.0.1       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.0.1/TinyTotp.jar) | [TinyTotp-4.0.1.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v4.0.1/TinyTotp-4.0.1.deb)
18+          | 3.2.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v3.2.0/TinyTotp.jar) | [TinyTotp-3.2.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v3.2.0/TinyTotp-3.2.0.deb)
18+          | 3.1.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v3.1.0/TinyTotp.jar) | [TinyTotp-3.1.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v3.1.0/TinyTotp-3.1.0.deb)
18+          | 3.0.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v3.0.0/TinyTotp.jar) | [TinyTotp-3.0.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v3.0.0/TinyTotp-3.0.0.deb)
18+          | 2.0.0       | [TinyTotp.jar](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v2.0.0/TinyTotp.jar) | [TinyTotp-2.0.0.deb](https://github.com/kryptonbutterfly/TinyTotp/releases/download/v2.0.0/TinyTotp-2.0.0.deb)

## Important

When starting the application you'll be prompted to enter a password.
This password is used to encryt/decrypt your totp secrets.
</br>**```Don't lose it!```**

## Issues & Solutions

<details>
    <summary>The generated password doesn't work.</summary>
    <p>
        <ul>
            <li>Ensure your system time is correct. If it's off by more than a couple of seconds the generated passwords will be wrong.</li>
            <li>
                Enable and configure a NTP server.<br/>
                <img src="https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/TimeServerExample.webp" alt="NTP Server settings"/>
            </li>
        </ul>
    </p>
</details>

<details>
    <summary>Could not find main class: kryptonbutterfly.totp.TinyTotp.</summary>
    <p>
        <img height="159px" weight="423px" src="https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/issues_and_solutions/java_no_main_class.png" alt="Error message"/>
        <h4>Solutions:</h4>
        <ul>
            <li>Ensure environment variable <code>PATH</code> is set correctly.</li>
            <li>Ensure environment varialbe <code>JAVA_HOME</code> is set correctly.</li>
            <li>Uninstall java, then install it again.</li>
        <ul>
    <p>
</details>


## Images

![Main Window](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Main.png)
<details>
<summary>more images</summary>

![Categories](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Categories.png)

![Import Secret](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Import-Secret.png)

![Preferences](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Preferences.webp)

![Import Secret via webcam](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Import-Qr.png)

![Add Totp Secret](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/AddSecret.png)

![Edit Totp Secret](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Edit-Totp.png)

![Export Secret](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Export-Secret.png)

![Set Icon](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Set-Icon.png)

![Select Icon](https://raw.githubusercontent.com/kryptonbutterfly/TinyTotp/master/md/Select-Icon.png)
</details>
