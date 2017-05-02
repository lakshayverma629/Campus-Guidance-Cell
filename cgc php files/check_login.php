<?php
$response = array();
include_once('connection.php'); 
  $user=isset($_POST['username']) ? $_POST['username'] : '';
  $pass = isset($_POST['password']) ? $_POST['password'] : '';

// get all products from products table
$result = mysql_query("SELECT * FROM login where username='$user' and password='$pass' ") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    
    // echo no users JSON
    echo json_encode($response);
}
?>
