<h1>RSA Encryption/Decryption Implementation in Java</h1>

<h2>Installation</h2>
<p>You can use your favorite IDE to run the project or you can use the following commands in your
command line / terminal:</p>
<ol>
    <li><code>javac src/RSA.java</code></li>
    <li><code>java src/RSA</code></li>
</ol>

<h2>Usage</h2>
<p>The application has 3 options: <u>e</u>ncrypt, <u>d</u>ecrypt, and <u>c</u>rack. When you run the code it will
ask you to input one of the options. Then you should input the path to your file (preferably the absolute path).</p>
<p>
<b>For encryption:</b> file extension should be .txt. First line must be reserved for e and n, in this order.
The rest of the file is considered as the message to be encrypted.<br>
<b>For decryption:</b> file extension should be .rsa. User will be asked to enter the private key d and n using the keyboard.
<br>
<b>For cracking:</b> file extension should be .rsa. User will be asked to enter the public key e and n using the keyboard</p>

<h2>Constraints</h2>
<p>This Java project is using the <code>long</code> integer data type, with values that  range  between
-9,223,372,036,854,775,808 and 9,223,372,036,854,775,807.</p>
<p>The alphabet dictionary used in this application is intentionally limited to:</p>
<ul>
    <li>52 letters (capital letters and small letters),</li>
    <li>10 digits (0..9),</li>
    <li>Punctuation marks: '.', '?', '!', ',', ';', ':', '-', '(', ')', '[', ']', '{', '}', ''', '"', '\space',
    '\newline'</li>
</ul>