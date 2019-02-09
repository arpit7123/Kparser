<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="generator" content="Bootply" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">

<title>K-Parser</title>

<!-- Icon for the web-page -->
<link href="css/img/icon.ico" rel="icon" type="image/x-icon" />

<link rel="stylesheet" type="text/css" href="css/styles.css">
<link rel="stylesheet" type="text/css" href="css/font-awesome.min.css">

<!-- Bootstrap Core CSS -->
<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">

<!-- D3 css -->
<link rel="stylesheet" type="text/css" href="css/dagre-d3-simple.css">

<!-- css for overlay -->
<link rel="stylesheet" type="text/css" href="css/normalize.css" />
<link rel="stylesheet" type="text/css" href="css/demo.css" />
<link rel="stylesheet" type="text/css" href="css/overlayStyle.css" />

<!--[if lt IE 9]>
			<script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
		<![endif]-->

</head>
<body>
	<header class="navbar navbar-inverse navbar-static-top" role="banner">
		<div class="container container_new">
			<div class="navbar-header">
				<button class="navbar-toggle" type="button" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a href="http://www.kparser.org" class="navbar-brand title"
					title="Knowledge Parser">Knowledge Parser</a>
			</div>
			<nav class="collapse navbar-collapse" role="navigation">
				<ul class="nav navbar-nav" id="navigation">
					<li><a href="/kparser/about.jsp" title="About Knowledge Parser">About</a></li>
					<li><a href="/kparser/api.jsp" title="API for Knowledge Parser">API</a></li>
					<li><a href="#" title="Contact Us">Contact</a></li>
				</ul>
			</nav>
		</div>
	</header>

	<!-- Begin Body -->
	<div class="container container_api">
		<div class="row">
			<div>
				<div id="about">
					<h1 style="text-align: center">Web API for K-Parser</h1>
					We have also developed a REST based Web API for K-Parser that
					allows the users to extract the output of K-Parser i.e. a directed
					acyclic graph for a given input sentence in RDF style <i>has</i>
					triplets. It is a nice way to access the output of K-Parser from a
					program. <br>
					<h3>Input and Output of the API:</h3>
					The API lets users to provide an input sentence (English sentence)
					and outputs in a JSON object the semantic graph. <br> <b>Example:</b>
					<br>Sentence:
					<div style="width: 500px; overflow: auto">
						<pre>
							<i>"John loves Mia."</i>
						</pre>
					</div>
					<br>API Output:
					<div style="width: 500px; overflow: auto">
						<pre>
{
  "graph": [
    "has(loves_2,agent,John_1).",
    "has(loves_2,recipient,Mia_3).",
    "has(loves_2,instance_of,love).",
    "has(love,is_subclass_of,emotion).",
    "has(John_1,instance_of,John).",
    "has(John,is_subclass_of,person).",
    "has(Mia_3,instance_of,Mia).",
    "has(Mia,is_subclass_of,person).",
    "has(John_1,semantic_role,:lover).",
    "has(Mia_3,semantic_role,:loved)."
  ],
  "sentence": "John loves Mia."
}
</pre>
					</div>

					<h3>How to use the API:</h3>
					Currently there is only one function available i.e lookup.
					Following url can be used to extract the JSON object of the parsed
					graph: <br> <i>http://bioai8core.fulton.asu.edu/KparserAPI/apiInput=/<b><font
							color="blue">sentence</font></b></i> <br>Here, <b><font
						color="blue">sentence</font></b> can be replaced my any English
					sentence. <br> <b>For example:</b> <br>API can be used
					for the sentence <i>"Every boxer walks."</i> by using the URL: <br>
					<a
						href="http://bioai8core.fulton.asu.edu/KparserAPI/apiInput=/Every boxer walks."><i>http://bioai8core.fulton.asu.edu/KparserAPI/apiInput=/<b>Every
								boxer walks.</b></i></a>

				</div>

			</div>
		</div>
	</div>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>

	<!-- <script type="text/javascript" src="resources/scripts/jquery-1.4.3.min.js"></script> -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
	<!-- <script src="resources/bootstrap.min.js"></script> -->
	<!-- <script src="js/scripts.js"></script>
<script src="js/kparserScript.js"></script> -->

	<script>
		/* $('#sidebar').affix({
		      offset: {
		        top: 245
		      }
		}); */
		/* $(document).ready(function() {
			$(".navbar").affix();

		}); */
		/* $(function(){
		    $('.sidebar').affix();
		}) */
	</script>
</body>
</html>
