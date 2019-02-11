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
				<a href="http://www.kparser.org" class="navbar-brand title">Knowledge
					Parser (K-Parser)</a>
			</div>
			<nav class="collapse navbar-collapse" role="navigation">
				<ul class="nav navbar-nav" id="navigation">
					<li><a href="/kparser/about.jsp" title="About Knowledge Parser">About</a></li>
					<!-- <li><a href="/kparser/api.jsp" title="API for Knowledge Parser">API</a></li> -->
					<li><a href="https://github.com/arpit7123/K-Parser-JAR" title="K-Parser JAR">K-Parser JAR</a></li>
					<li><a href="https://github.com/arpit7123/Kparser" title="Knowledge Parser Github Link">GitHub Code Repository</a></li>
					<li><a href="https://arpit7123.github.io/" title="Contact Information">Contact Information</a></li>
				</ul>
			</nav>
		</div>
	</header>

	<!-- Begin Body -->
	<div class="container container_new">
		<div class="row">
			<div class="col-md-3" id="leftCol">
				<div class="well">
					<h2><span class="label label-primary">Example Sentences</span></h2>
					<div>
					<h4><a id="getoggler" href="#" data-toggle="collapse"
						data-target="#generalExamples"> <span class="label label-warning">General &raquo;</span></a></h4>
					<div id="generalExamples" class="collapse">
						<ul class="nav nav-stacked" id="examples">
							<li><a class="deselected">Every boxer walks.</a></li>
							<li><a class="deselected">Some boxer walks.</a></li>
							<li><a class="deselected">John loves his wife.</a></li>
							<li><a class="deselected">The boy wants to visit New York City.</a></li>
							<li><a class="deselected">The accident happened as the night was falling.</a></li>
							<li><a class="deselected">Tom killed the boy.</a></li>
							<li><a class="deselected">If John had not loved every cat, he would have loved a dog.</a></li>
							<li><a class="deselected">Fish ate the worm because it was tasty.</a></li>
							<li><a class="deselected">The older students were bullying the younger students so we rescued them.</a></li>
							<li><a class="deselected">My dog likes eating sausage.</a></li>
							<li><a class="deselected">Gavin ran away after he killed Tom.</a></li>
							<li><a class="deselected">My dog also likes eating sausage.</a></li>
							<li><a class="deselected">He was shot by a professional killer.</a></li>
							<li><a class="deselected">She sat opposite him and looked into his eyes.</a></li>
							<li><a class="deselected">He fell asleep during the meeting.</a></li>
							<li><a class="deselected">The river runs beside our house.</a></li>
							<li><a class="deselected">She is like her sister.</a></li>
							<!--<li><a href="#sec16">The mouse ran under the chair.</a></li>-->
						</ul>
					</div>
					</div>

					<div>
						<h4><a id="eetoggler" href="#" data-toggle="collapse"
							data-target="#eventExamples"> <span class="label label-warning">Events &raquo;</span></a></h4>
						<div id="eventExamples" class="collapse">
							<ul class="nav nav-stacked" id="examples">
								<li><a class="deselected">John ran.</a></li>
								<li><a class="deselected">Mary walked.</a></li>
								<li><a class="deselected">Mary walked to the store.</a></li>
								<li><a class="deselected">Tim ran two miles.</a></li>
								<li><a class="deselected">John arrived at his destination.</a></li>
								<li><a class="deselected">John loves Mia.</a></li>
								<li><a class="deselected">I knew about the incident.</a></li>
								<li><a class="deselected">He fell asleep during the meeting.</a></li>
								<li><a class="deselected">The knife was used to kill the dog.</a></li>
								<li><a class="deselected">George was bullying Tim so we rescued him.</a></li>
								<li><a class="deselected">John loves Mia, and Mia hates John.</a></li>
								<li><a class="deselected">Tom killed John before Tom and Jane ran away.</a></li>
								<li><a class="deselected">She sat opposite him and looked into his eyes.</a></li>
							</ul>
						</div>
					</div>

					<div>
					<h4><a id="qetoggler" href="#" data-toggle="collapse"
						data-target="#questionExamples"> <span class="label label-warning">Questions &raquo;</span></a></h4>
					<div id="questionExamples" class="collapse">
						<ul class="nav nav-stacked" id="examples">
							<li><a class="deselected">What is your name ?</a></li>
							<li><a class="deselected">Did she like it ?</a></li>
							<li><a class="deselected">How are you ?</a></li>
							<li><a class="deselected">Who killed the dog ?</a></li>
							<li><a class="deselected">Did you eat all the apples ?</a></li>
						</ul>
					</div>
					</div>
					
					

					<div>
						<h4><a id="steptoggler" href="#" data-toggle="collapse"
							data-target="#stepExamples"> <span class="label label-warning">STEP 2008 Task Examples &raquo;</span></a></h4>
						<div id="stepExamples" class="collapse">
							<ul class="nav nav-stacked" id="examples">
								<li><a class="deselected">The atmosphere was warm and friendly.</a></li>
								<li><a class="deselected">The gun crew was killed, they were crouching unnaturally.</a></li>
								<li><a class="deselected">Researchers have been looking for other cancers that may be caused by viruses.</a></li>
								<li><a class="deselected">In Mortagua, Joao Pedro Fonseca and Marta Gomes coordinate the project that seven people develop in this school.</a></li>
								<li><a class="deselected">In the mid-80s, wind turbines had a typical maximum power rating of 150 kW.</a></li>
								<li><a class="deselected">The waiter took the order.</a></li>
								<li><a class="deselected">He began to read his book.</a></li>
								<li><a class="deselected">What is the duration of the fall?</a></li>
							</ul>
						</div>
					</div>

					
				</div>

			</div>
			

			<div class="col-md-9">
				<form role="form" id="input-form">
					<div class="form-group">
						<textarea class="form-control" id="sentence_input" rows="1"
							placeholder="Type or paste sentence to parse here (Click on Parse button to submit)."></textarea>
					</div>
					<div class="checkbox" style="float:right;">
						<label><input type="checkbox" id="checkCoreference"
							name="" value="CheckCoreference" class="chk">Check
							Coreference</label>
					</div>
					<button type="reset" class="btn btn-default" id="Clear">Clear</button>
					<button type="submit" class="btn btn-primary" id="Process" name=""
						value="Process">Parse</button>
				</form>
				
				<div id="information" class="alert alert-danger" role="alert"
					style="display: none">
					<strong>Error!</strong> Please enter a valid sentence
				</div>

				<div class="btn-group">
					<h4>
						<span class="label label-default event">Event</span> <span
							class="label label-default entity">Entity</span> <span
							class="label label-default class">Class</span> <span
							class="label label-default semRole">Semantic Role</span> <span
							class="label label-default coref">Co-referents</span>
					</h4>
				</div>
				<div class="overlay overlay-hugeinc" id="attach">
					<button type="button" class="overlay-close" id="closeButton"
						title="close">Close</button>
					<svg class="main-svg" id="svg-canvas"></svg>
				</div>
				<div class="main-svg1-button" id="attach1">
					<button class="main-svg1-fullscreen" type="button"
						id="trigger-overlay" title="fullscreen">Fullscreen</button>
					<svg class="main-svg1" id="svg-canvas1"></svg>
				</div>
			
				<div style="width: 50%; float:left;"><!-- class="col-md-5"> -->
					<div class="form-group">
					    <label>JSON Format</label>
					    <button class="btn btn-default" id="json_expand_btn" type="submit">Expand</button>
					    <textarea class="collapse" id="json_output" disabled="disabled" rows="19" cols="40" placeholder="NULL" style="overflow:auto;resize:none"></textarea>
					</div>
				</div>
				<div style="width: 50%; float:right;"><!-- class="col-md-4"> -->
					<div class="form-group">
					    <label>QASRL Format</label>
					    <button class="btn btn-default" id="vqa_expand_btn" type="submit">Expand</button>
					    <textarea class="collapse" id="vqa_output" disabled="disabled" rows="19" cols="40" placeholder="NULL" style="overflow:auto;resize:none"></textarea>
					</div>
				</div>
				<br>
				<br>
				<br>
				<br>
			</div>
		</div>
	</div>

<!-- script references -->
<script src="resources/modernizr.custom.js"></script>
<script type="text/javascript" src="resources/scripts/jquery-1.4.3.min.js"></script>
<script type="text/javascript" src="resources/splitter.js"></script>
<script type="text/javascript" src="resources/d3.min.js"></script>
<script type="text/javascript" src="resources/dagre.min.js"></script>
<script src="resources/dagre-d3-simple.js"></script>
<!-- <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script> -->
<!-- <script src="resources/bootstrap.min.js"></script> -->
<!-- <script src="js/scripts.js"></script>
<script src="js/kparserScript.js"></script> -->
<script src="resources/classie.js"></script>
<script src="resources/demo1.js"></script>
<script>
	
	$(document).ready(function() {
		$("#getoggler").click(function() {
			$("#generalExamples").toggleClass('collapse');
		});

	});
	
	$(document).ready(function() {
		$("#eetoggler").click(function() {
			$("#eventExamples").toggleClass('collapse');
		});

	});
	
	$(document).ready(function() {
		$("#qetoggler").click(function() {
			$("#questionExamples").toggleClass('collapse');
		});

	});
	
	$(document).ready(function() {
		$("#steptoggler").click(function() {
			$("#stepExamples").toggleClass('collapse');
		});

	});

	$(document).ready(function() {
		$("#json_expand_btn").click(function() {
			//if($('#json_output').is("collapse")){
			$('#json_output').toggleClass("collapse");
			//}
			/* if($('#json_output').is("expand")){
				$('#json_output').removeClass("expand").addClass("collapse");
			} */
			return false;
		});
	});
	
	$(document).ready(function() {
		$("#vqa_expand_btn").click(function() {
			//if($('#json_output').is("collapse")){
			$('#vqa_output').toggleClass("collapse");
			//}
			/* if($('#json_output').is("expand")){
				$('#json_output').removeClass("expand").addClass("collapse");
			} */
			return false;
		});
	});

	$(document).ready(function() {
		$('ul#examples li').click(function() {
			$('div#information').hide();
			$("#svg-canvas").empty();
			$("#svg-canvas1").empty();
			$(this).removeClass("deselected").addClass("selected");
			var text = $(this).text();
			var useCoreference = $('.chk').is(":checked");
			$("#sentence_input").val(text);
			$('body').css('cursor', 'progress');
			submitText(text, useCoreference);
		});
	});
	$(document).ready(function() {
		$("#Process").click(function() {
			$('#json_output').removeClass("expand").addClass("collapse");
			$('#vqa_output').removeClass("expand").addClass("collapse");
			$('div#information').hide();
			$("#svg-canvas").empty();
			$("#svg-canvas1").empty();
			var input = $("#sentence_input");
			var text = input.val();
			var useCoreference = $('.chk').is(":checked");
			$('body').css('cursor', 'progress');
			submitText(text, useCoreference);
			return false;
		});
	});

	function submitText(text, useCoreference) {
		$("#svg-canvas").empty();
		$("#svg-canvas1").empty();
		$.ajax({
			type : 'GET',
			dataType : 'html',
			url : "ParserServlet",
			data : {
				text : text,
				useCoreference : useCoreference
			},
			success : function(data) {
				$('#information').hide();
				if (data == "null") {
					$('div#information').show();
				} else if (data == "[]") {
					$('div#information').show();
				} else {
					var allData = JSON.parse(data);
					
					var vqaOutput = allData.vqa_data;
					vqaOut = JSON.stringify(vqaOutput, undefined, 4);
					//$('#vqa_output').val(vqaOut);
					
					var qasrl = "";
					var i;
					for(i=0;i<vqaOutput.length;i++){
						qasrl += "Verb: " + vqaOutput[i].verb + "\n";
						var qa = vqaOutput[i].qas;
						qasrl += "Question Answers:\n" + JSON.stringify(qa, undefined, 4) + "\n\n";
						
					}
					
					$('#vqa_output').val(qasrl);
					
					var dataParsed = allData.parser_output;
					//alert(dataParsed);

					var jsonOutput = JSON.stringify(dataParsed, undefined, 4);
					$('#json_output').val(jsonOutput);/* output(str); */
				
					var nodes = {};
					var edges = [];

					dataParsed.forEach(function(e) {
						populate(e, nodes, edges);
					});

					renderJSObjsToD3(nodes, edges, ".main-svg1");
					renderJSObjsToD3(nodes, edges, ".main-svg");
				}
			},
			complete : function() {
				$('body').css('cursor', 'default');
			}
		});
	}

	function output(inp) {
		document.body.appendChild(document.createElement('pre')).innerHTML = inp;
	}

	function populate(data, nodes, edges) {
		var nodeID = Object.keys(nodes).length;

		var newNode = {};
		var initLabel = data.data.word;
        if(data.data.pos === "VB" && !data.data.isClass){
            initLabel += " (Tense:present)";
        }else if(data.data.pos === "VBD" && !data.data.isClass){
            initLabel += " (Tense:past)";
        }else if(data.data.pos === "VBG" && !data.data.isClass){
            initLabel += " (Tense:present participle)";
        }else if(data.data.pos === "VBN" && !data.data.isClass){
            initLabel += " (Tense:past participle)";
        }else if(data.data.pos === "VBP" && !data.data.isClass){
            initLabel += " (Tense:present)";
        }else if(data.data.pos === "VBZ" && !data.data.isClass){
            initLabel += " (Tense:present)";
        }
		if (data.data.wordSense === undefined) {
			newNode = {
				label : initLabel,//data.data.word,//(data.data.type == "TK") ? data.data.word : data.data.type,
				pos : data.data.pos,
				id : nodeID + "",
				isClass : data.data.isClass,
				isEvent : data.data.isEvent,
				isEntity : data.data.isEntity,
				isASemanticRole : data.data.isASemanticRole,
				isACoreferent : data.data.isACoreferent
			};
		} else {
			newNode = {
				label : initLabel,//data.data.word,// +' WS:'+ data.data.wordSense,//(data.data.type == "TK") ? data.data.word : data.data.type,
				pos : data.data.pos,
				id : nodeID + "",
				isClass : data.data.isClass,
				isEvent : data.data.isEvent,
				isEntity : data.data.isEntity,
				isASemanticRole : data.data.isASemanticRole,
				isACoreferent : data.data.isACoreferent
			};
		}

		if (data.data.wordSense) {
			newNode.wordSense = data.data.wordSense;
		}

		if (data.data.Edge) {
			newNode.edge = data.data.Edge;
		}

		/* var classes = [ "type-" + data.data.Edge ];
		if (data.data.pos) {
			classes.push("ne-" + data.data.ne);
		}

		newNode.nodeclass = classes.join(" ");
		 */
		//  I hate javascript
		nodes[nodeID] = newNode;

		data.children.forEach(function(child) {
			var newChild = populate(child, nodes, edges);

			edges.push({
				source : newNode.id,
				target : newChild.id,
				id : newNode.id + "-" + newChild.id,
				label : newChild.edge
			});
		});

		return newNode;
	}

	function buildGraphData(node, nodes, links) {

		var index = nodes.length;
		nodes.push({
			name : node.data.content,
			group : 1
		});

		node.children.forEach(function(e) {
			links.push({
				source : index,
				target : nodes.length,
				value : 2
			});
			buildGraphData(e, nodes, links);
		});
	}
</script>
</body>
</html>
