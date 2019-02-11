<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="generator" content="Bootply" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	
<title>K-Parser</title>

<!-- Icon for the web-page -->
<link href="css/img/icon.ico" rel="icon" type="image/x-icon" />

<link rel="stylesheet" type="text/css" href="css/styles.css" >
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
				<a href="http://www.kparser.org" class="navbar-brand title" title="Knowledge Parser">Knowledge
					Parser</a>
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
	<div class="container container_new">
		<div class="row">
			<div class="col-md-3" id="leftCol">
				<div class="well affix">
					<div class="nav nav-stacked" id="sidebar">
						<h3>
							<a id="getoggler" href="#about" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">About Knowledge Parser</span></a>
						</h3>
						<h3>
							<a id="getoggler" href="#versions" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Versions</span></a>
						</h3>
						<h3>
							<a id="getoggler" href="#applications" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Applications</span></a>
						</h3>
						<h3>
							<a id="getoggler" href="#publications" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Publications</span></a>
						</h3>

					</div>
				</div>

			</div>

			<div class="col-md-9">
				<div id="about">
					<h1 style="text-align: center">About Knowledge Parser
						(K-Parser)</h1>
					<p>
						Knowledge Parser or K-Parser is a semantic parser that translates
						the input text into directed acyclic graph. The nodes in the graph
						represent the actual words in the input text and the conceptual
						classes of those words. The edges represent the dependency between
						different nodes and the edge labels in the graph represent the
						semantic relations between the nodes. The relation labels are
						inspired from <a
							href="http://www.cs.utexas.edu/users/mfkb/RKF/tree/">Knowledge
							Machine</a> ontology. An example of the output semantic
						representation for the sentence <i>"Barack Obama signed the
							new reform bill"</i> is shown in Figure. 1 below. The parser is
						composed of many features and components that are used to attain
						those features. A list of such features is shown in table 1.
					</p>

					<img
						src="/kparser/images/example1.png"
						border="5" class="imgborder" title="K-Parser example"
						alt="K-Parser Example">
					<h5 style="text-align: center">
						K-Parser output for <i>"Barack Obama signed the new reform
							bill."</i>
					</h5>
				</div>
				<div id="versions">
					<h1 style="text-align: center">Versions of K-Parser and
						Features</h1>
					We have developed following versions of K-Parser until now with the
					mentioned features:
					<ul>
						<li><a href="#">Version 1</a></li>
						<li><a href="#">Version 2</a></li>
						<li><a href="#">Version 3</a></li>
					</ul>
				</div>


				<div id="applications">
					<h1 style="text-align: center">Applications of K-Parser</h1>
					We have used K-Parser in multiple applications, some are mentioned
					below:
					<ul>
						<li><a href="#">Solving The Winograd Schema Challenge</a></li>
						<li><a href="#">Visual Understanding System</a></li>
						<li><a href="#"></a></li>
					</ul>
				</div>
				<div id="publications">
					<h1 style="text-align: center">Publications of K-Parser</h1>
					<ul>
					<li>Arpit Sharma, Nguyen Vo, Shruti Gaur and Chitta Baral. <a href="http://www.aaai.org/ocs/index.php/SSS/SSS15/paper/viewFile/10299/10087">"An
						Approach to Solve Winograd Schema Challenge Using Automatically
						Extracted Commonsense Knowledge."</a> In AAAI Spring Symposium,
						Logical formulations of commonsense reasoning, 2015.</li>
						
					<li>Arpit Sharma, Nguyen H. Vo, Somak Aditya and Chitta Baral.
						<a href="http://www.public.asu.edu/~cbaral/papers/event2015.pdf">"Identifying Various Kinds of Event Mentions in K-Parser Output."</a>
						In Proceedings of NAACL 2015 The 3rd Workshop on Events:
						Definition, Coreference, and Representation, 2015</li>

					<li>Arpit Sharma, Nguyen H. Vo, Somak Aditya and Chitta Baral.
						<a href="http://www.public.asu.edu/~cbaral/papers/ijcai2015.pdf">"Towards Addressing the Winograd Schema Challenge - Building and
						Using a Semantic Parser and a Knowledge Hunting Module."</a> In
						Proceedings of Twenty-Fourth International Joint Conference on
						Artificial Intelligence. AAAI Press, 2015.</li>
						</ul>
				</div>

			</div>
		</div>
	</div>
	
	
<!-- <script type="text/javascript" src="resources/scripts/jquery-1.4.3.min.js"></script> -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
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
