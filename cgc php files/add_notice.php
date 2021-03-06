<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();
include_once('connection.php');

// check for required fields
if (isset($_POST['description'])) {
    $name = isset($_POST['name']) ? $_POST['name'] : '';
    $description = isset($_POST['description']) ? $_POST['description'] : '';
 	$type= isset($_POST['type']) ? $_POST['type'] : '';
	$username= isset($_POST['username']) ? $_POST['username'] : '';
	$image='null';
    $response['file_name'] = basename($_FILES['image']['name']);
    $response['name'] = $name;
	
    $response['description'] = $description;

    // mysql inserting a new row

 		 	$result = mysql_query("INSERT INTO notice(subject, description,image_name,username,type) VALUES('$name', '$description', '$image','$username','$type')");


   
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Notice added succesfully..";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>