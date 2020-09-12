<?php
$message=$_GET["message"];

$redis= new Redis();
$redis->connect("127.0.0.1", 6379);
$redis->publish("Message", $message);
?>
