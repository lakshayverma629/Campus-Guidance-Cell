<?php
 
// Path to move uploaded files
$target_path = "uploads/";
 
// array for final json respone
$response = array();
 
// getting server ip address
$server_ip = gethostbyname(gethostname());
 
// final file url that is being uploaded
$file_upload_url = 'http://' . $server_ip . '/' . '/cgc' . '/' . $target_path;
include_once('connection.php');  
  
if (isset($_FILES['image']['name'])) {
    $target_path = $target_path . basename($_FILES['image']['name']);
 
    // reading other post parameters
    $name = isset($_POST['name']) ? $_POST['name'] : '';
    $description = isset($_POST['description']) ? $_POST['description'] : '';
 	$type= isset($_POST['type']) ? $_POST['type'] : '';
	$username= isset($_POST['username']) ? $_POST['username'] : '';
	
    $response['file_name'] = basename($_FILES['image']['name']);
    $response['name'] = $name;
	
    $response['description'] = $description;
 		 	$result = mysql_query("INSERT INTO notice(subject, description,image_name,username,type) VALUES('$name', '$description', '$image','$username','$type')");

    try {
        // Throws exception incase file is not being moved
        if (!move_uploaded_file($_FILES['image']['tmp_name'], $target_path)) {
            // make error flag true
            $response['error'] = true;
            $response['message'] = 'Could not move the file!';
        }
 
        // File successfully uploaded
        $response['message'] = 'File uploaded successfully!';
        $response['error'] = false;
        $response['file_path'] = $file_upload_url . basename($_FILES['image']['name']);
    } catch (Exception $e) {
        // Exception occurred. Make error flag true
        $response['error'] = true;
        $response['message'] = $e->getMessage();
    }
} else {
    // File parameter is missing
    $response['error'] = true;
    $response['message'] = 'Not received any file!F';
}
 
// Echo final json response to client
echo json_encode($response);
?>
