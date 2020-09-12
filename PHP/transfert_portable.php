<?php
/*
Ce scénario php permet de récupérer l'état de transfert vers le portable, si demandé, il peut activer ou désactiver le transfert vers le portable Thibault ou Bertrand.
Alogrithme: Dans le fichier etat_transfert.txt, on enregistre l'état de transfert. 0: Pas de transfert, 1:Vers Thibault, 2:Vers Bertrand. Chaque fois qu'on active ou désactive le transfert, on modifie le contenant du fichier etat_transfert.txt. 
*/

$redis = new Redis();
$redis->connect("127.0.0.1",6379);

function getStatus(){
	//Lire le fichier etat_transfert.txt
	$file=fopen("./etat_transfert.txt","r+");
	$status=fgetc($file);
	fclose($file);
	return $status;
}

function setTOn(){
	//Ouvrir etat_transfert.txt puis modifie le contenant
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"1");
	fclose($fh);

	//Activer le transfert vers Thibault
	fopen('http://admin:admgm@192.168.0.100/servlet?forward=set&Forward_always_enable=1&Forward_always_target=00616194031','r');
	$redis = new Redis();
	$redis->connect("127.0.0.1",6379);
	$redis->publish("TransfertPortable","TActived");
}

function setTOff(){
	//Ouvrir etat_transfert.txt puis modifie le contenant
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"0");
	fclose($fh);

	//Désactiver le transfert
	fopen('http://admin:admgm@192.168.0.100/servlet?forward=set&Forward_always_enable=0','r');
	$redis = new Redis();
	$redis->connect("127.0.0.1",6379);
	$redis->publish("TransfertPortable","Desactived");
}

function setBOn(){
	//Ouvrir etat_transfert.txt puis modifie le contenant
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"2");
	fclose($fh);
	//Activer le transfert vers Bertrand
	fopen('http://admin:admgm@192.168.0.100/servlet?forward=set&Forward_always_enable=1&Forward_always_target=00674690975','r');
	$redis = new Redis();
	$redis->connect("127.0.0.1",6379);
	$redis->publish("TransfertPortable","BActived");
}

function setBOff(){
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"0");
	fclose($fh);
	//Désactiver le transfert
	fopen('http://admin:admgm@192.168.0.100/servlet?forward=set&Forward_always_enable=0','r');
	$redis = new Redis();
	$redis->connect("127.0.0.1",6379);
	$redis->publish("TransfertPortable","Desactived");
}

//Si on active ou desactive un transfert directement depuis le téléphone, il faut modifier l'état enregistré dans etat_transfert.txt
function telSetTOn(){
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"1");
	fclose($fh);
}

function telSetBOn(){
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"2");
	fclose($fh);
}

function telSetOff(){
	$fh = fopen( './etat_transfert.txt', 'w' );
	fwrite($fh,"0");
	fclose($fh);
}

$toDo=$_GET["opt"];

if($toDo=="telTActive"){
	telSetTOn();
}

if($toDo=="telBActive"){
	telSetBOn();
}

if($toDo=="telDesactive"){
	telSetOff();
}

if($toDo=="Tactive"){
	if(getStatus()!='1'){
		echo("Yes,Active T!");
		setTOn();
	}
	else{
		echo("T Already actived...");
	}
}
else if($toDo=="Bactive"){
	if(getStatus()!='2'){
		echo("Yes,Active B!");
		setBOn();
	}
	else{
		echo("B Already actived...");
	}
}
else if($toDo=="desactive"){
	if(getStatus()=='1'){
		echo("Yes,Desactive T!");
		setTOff();
	}
	else if(getStatus()=='2'){
		echo("Yes,Desactive B!");
		setBOff();
	}else if(getStatus()=='0'){
		echo("Already desactived...");
	}
}

if($toDo=="showStatus"){
	$status=getStatus();
	if($status=='0'){
		echo("noTransfer");
	}
	else if($status=='1'){
		echo("toT");
	}else if($status=='2'){
		echo("toB");
	}
}

?>
