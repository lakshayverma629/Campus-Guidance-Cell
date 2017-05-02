<?php

/*
 * Following code will list all the products
 */

// array for JSON response
$response = array();
$id = isset($_POST['id']) ? $_POST['id'] : '';
   
include_once('connection.php');
// get all products from products table
$result = mysql_query("delete from notice where id='$id'") or die(mysql_error());

// check for empty result
if ($result) {
      
    // success
    $response["success"] = 1;
	$response["message"]="Notice deleted";
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No Notices found";

    // echo no users JSON
    echo json_encode($response);
}
?>
