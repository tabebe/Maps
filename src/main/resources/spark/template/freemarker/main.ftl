<!DOCTYPE html>
  <head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <link rel="stylesheet" href="/css/bootstrap.min.css">

  </head>
  <body>
    <header>
      <h1>Maps</h1>
    </header>
    
    <main class="container">
    <div>
    <div class="col-sm-2" id="intersection_form">
    <div class="intersection">
      
        <h3 class="text-center info">Street1</h3>

          <div id="start_1" class="">
            <input type="text" id="input_field_start_1"  placeholder="Street1"  name="start_1"></input>
          </div>

          <h3 class="text-center info">CrossStreet1</h3>
        
          <div id="start_2" class="right ">
            <input type="text" id="input_field_start_2" placeholder="Cross Street1"  name="start_2"></input>
          </div>
      </div>

      

      
    </div>
    <div id="map" class="col-sm-8">
    
    
    </div>
    
    
<br/>

      <div class="intersection">
        <h3 class="text-center info">Street2</h3>

          <div id="end_1" class="">
            <input type="text" id="input_field_end_1"  placeholder="Street2"  name="end_1"></input>
            
          </div>

          <h3 class="text-center info">CrossStreet2</h3>
        
          <div id="end_2" class="right ">
            <input type="text" id="input_field_end_2" placeholder="Cross Street2"  name="end_2"></input>
            
          </div>
      </div>
      <br/>
   <!-- <button id="clear_button" onclick="clear_path()">Clear Path</button> -->
    
    
    <div class="intersection">
        <button class=" btn btn-primary search">Search</button>
      </div>
    
    </main>

    <footer>
      
    </footer>

     <script src="js/jquery-2.1.1.js"></script>
     <script src="js/main.js"></script>
  </body>
</html>
