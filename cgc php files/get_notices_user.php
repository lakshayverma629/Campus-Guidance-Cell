<?php

/*
 * Following code will list all the products
 */

// array for JSON response
$response = array();
$user = isset($_POST['user']) ? $_POST['user'] : '';
   
include_once('connection.php');
// get all products from products table
$result = mysql_query("SELECT * FROM notice where username='$user' order by created_at desc") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // products node
    $response["products"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $product = array();
        $product["id"] = $row["id"];
        $product["subject"] = $row["subject"];
        $product["image_name"] = $row["image_name"];
		$product["username"] = $row["username"];
        $product["type"] = $row["type"];
        $product["description"] = $row["description"];
        $product["created_at"] = $row["created_at"];
        $product["updated_at"] = $row["updated_at"];

        // push single product into final response array
        array_push($response["products"], $product);
    }
    // success
    $response["success"] = 1;

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
