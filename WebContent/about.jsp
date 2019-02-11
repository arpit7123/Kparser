<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta name="generator" content="Bootply" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">

<title>Knowledge-Parser</title>

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
				<a href="http://www.kparser.org" class="navbar-brand title" title="Knowledge Parser">Knowledge
					Parser (K-Parser)</a>
			</div>
			<nav class="collapse navbar-collapse" role="navigation">
				<ul class="nav navbar-nav" id="navigation">
					<li><a href="/kparser/about.jsp" title="About Knowledge Parser">About</a></li>
					<!-- <li><a href="/kparser/api.jsp" title="API for Knowledge Parser">API</a></li> -->
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
				<div class="well affix">
					<div class="nav nav-stacked" id="sidebar">
						<h3>
							<a id="getoggler" href="#about" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">About Knowledge Parser</span></a>
						</h3>
						<h3>
							<a id="getoggler" href="#semRelations" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Semantic Relations</span></a>
						</h3>
						<h3>
							<a id="getoggler" href="#adjectives" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Adjectives</span></a>
						</h3>
						<!-- <h3>
							<a id="getoggler" href="#applications" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Applications</span></a>
						</h3> -->
						<!-- <h3>
							<a id="getoggler" href="#publications" data-toggle="collapse"
								data-target="#generalExamples"> <span
								class="label label-default">Publications</span></a>
						</h3> -->

					</div>
				</div>

			</div>

			<div class="col-md-9">
				<div id="about">
					<h1 style="text-align: center">About Knowledge Parser</h1>
					<p>Knowledge Parser is a semantic parser that translates any English
						sentence into a directed acyclic semantic graph. The nodes in the
						graph represent the actual words in the input text and the
						conceptual classes of those words. The edges represent the
						dependency between different nodes and the edge labels in the
						graph represent the semantic relations between the nodes.
					<p>The figure below illustrates the basic algorithm of K-Parser with
						the help of an example sentence.</p>
					<!-- <img src="/kparser/images/system.png" border="5" -->
					<img
						src="./images/system.jpg"
						border="5" class="imgborder" title="K-Parser system algorithm"
						alt="K-Parser Example">
					<h5 style="text-align: center">
						System Algorithm explained with the help of the example sentence <i>"John
							cared for his lovely wife."</i>
					</h5>
					
					The parsing algorithm used in the K-Parser system is a pipeline of various components as explained below.

					<h3 style="text-align: left">A. Syntactic Dependency Extraction</h3>
					<p>We use a dependency parser to extract the dependency graph corresponding to an input sentence or question. The Stanford Dependency Parser \citep{de2008stanford} was used in this module. An example is shown in the Figure above.</p>
					
					<h3 style="text-align: left">B. Class Extraction</h3>
					<p>We use word sense disambiguation on the input text to determine the correct senses of content words in it. The senses are then used to identify the base forms of the words and also their conceptual types. The disambiguation algorithm mentioned in \citep{basile2007jigsaw} was used with respect to the WordNet \citep{miller1995wordnet} knowledge base. Base forms of words was also extracted from the WordNet KB. An example shown in the Figure above.</p>
					
					
					<h3 style="text-align: left">C. Rule-Based Mapping</h3>
					<p>We use a rule based transformation of a dependency parse into a semantic parse. A set of mapping rules were defined to map dependency relations into semantic relations. An example of a rule is as shown below.</p>
					
					<div style="width:600px;height:100px;border:1px solid #000;">
					<b>IF</b>
					<br><i>nsubj(N1,N2)</i> <b>AND</b> <i>pos(N1)==verb</i> <b>AND</b> <i>pos(N2)==noun</i> <b>AND</b> <i>type(N2)==person</i>
					<br><b>THEN</b>
					<br><i>has(N1,agent,N2)</i>
					</div>
					
					<br>
					<p>The above rule states that if any node <i>N2</i> is nominal subject (<i>nsubj</i>) of another node <i>N1</i>, <i>N1</i> is a verb, <i>N2</i> is a noun and <i>N2</i> is a <i>person</i> then, create a semantic relation called <i>agent</i> from <i>N1</i> to <i>N2</i> in the output semantic representation. There are a set of 92 such if-then rules in the current implementation of the parser. These rules cover all the syntactic dependency relations in the Stanford Dependency Parser version 3.4.1 except the ones which correspond to prepositions i.e. relations of the form <i>prep_*</i>.</p>
					 
					<h3 style="text-align: left">D. Prepositional Semantics</h3>
					<p>Many times the concepts in a text are connected to each other via prepositions. For example in the sentence ``John came by car.'' the connection between ``came'' and ``car''. We use a preposition sense disambiguation module to represent such dependencies in the output of K-Parser. We use an in-house trained model to distinguish between different senses of a preposition. Each sense corresponds to a semantic relation in the K-Parser relation library. The disambiguation model was trained on a corpus of 7000 sentences with respect to 29 most frequent prepositions from The Preposition Project \cite{litkowski2013preposition}. The test accuracy of 75\% was achieved on a set of 2000 sentences in the preposition sense disambiguation task.</p>
					
					<h3 style="text-align: left">E. Discourse Relations</h3>
					<p>We use shallow discourse parsing to add the semantic relations between two events (or actions) in the K-Parser output. A typical text consists of meaningful phrases that are glued together to form a coherent discourse. Shallow discourse parsing is the task of parsing a piece of text into a set of discourse relations (such as causal and temporal) between two adjacent or non-adjacent phrases. We use Penn Discourse Treebank style discourse parser \citep{lin2014pdtb} in K-Parser and add semantic relations between the actions (verbs) in each of the phrases identified by the discourse parser. An example sentence containing such a relation is <i>"She hated him because he killed a chicken."</i> The output of K-Parser could not be displayed here because of space restrictions but we encourage the reader to try it on the live demo of K-Parser at <i>www.kparser.org</i></p>
					
					<h3 style="text-align: left">F. Additional Knowledge Augmentation</h3>
					<p>We use the Propbank frame sets \citep{palmer2005proposition} to add the semantic roles to the entities in an input sentence or a question. The frames are defined in Propbank for each verb. These frames along with the semantic relations in it are used to identify the entity corresponding to a particular role. We also add the two levels of classes with respect to each of the nodes in the representation. The classes are extracted by the class extractor component explained above. Furthermore, we use Stanford co-reference resolution system \citep{raghunathan2010multi} as an optional co-reference resolver in the K-Parser system.% Many other NLP tools are also used for assistance in different steps for example named entity tagging is done as a part of pre-processing of text before providing it to the parsing algorithm.</p>


					<h2 style="text-align: center">Aspects of K-Parser And Its Output</h2>
					<h3 style="text-align: left">Readable Semantic Relations</h3>
					<p>The output of K-Parser contains semantic relations that are easily understandable by people without domain specific knowledge. For example, the relation <i>recipient</i> from a verb (say <i>v1</i>) to a noun (say <i>n1</i>) means that <i>n1</i> is the receiver of the action <i>v1</i>.</p> 
					
					<h3 style="text-align: left">Additional Knowledge</h3>
					<p>The output of K-Parser contains the knowledge that is not explicitly present in the input text. For example, the semantic roles of entities (e.g., <i>John</i> is <i>lover</i> in <i>John loves Tom</i>), the 'type' information about a concept (e.g., <i>'John'</i> is a <i>'person'</i>) and an optional co-reference resolution (e.g., <i>John</i> and <i>his</i> refer to a common entity in <i>John loves his wife</i>).</p>
					
					<h3 style="text-align: left">Preposition Relations</h3>
					<p>The output of the K-Parser differentiates between different senses of a common preposition by using different semantic relations. For example the sentences <i>"John came at 5pm"</i> and <i>"John came at the location"</i> have different relations even though there is only one preposition i.e., <i>'at'</i>.</p>
					
					<h3 style="text-align: left">Event-Event Relations</h3>
					<p>The output of the K-Parser also contains the relationship between two events (verb nodes) if it is present in the input sentence. It is an important feature that enables one to link two discourses and helps in the automated reasoning in problems such as the winograd schema challenge \cite{sharma2015approach}.</p>
					
					<h3 style="text-align: left">Limited Quantification</h3>
					<p>The quantification of entities is an important problem in representing natural language utterances. For example, it is important to differentiate between the semantic representations of <i>"Every man has legs"</i> and <i>"A man had 3 thumbs"</i>. The output of the K-Parser differentiates such sentences by using a prototypical inheritance and a classical inheritance relationship between a concept and its type (class).</p>
					
					<h3 style="text-align: left">Question Parsing</h3>
					<p>The K-Parser system can semantically parse four different types of questions. The types include <i>Wh- Questions</i> (e.g., <i>Who rescued the dog?</i>), <i>Yes/No Questions</i> (e.g., <i>Is it true?</i>), <i>Tag Questions</i> (e.g., <i>You love her, don’t you?</i>) and <i>Choice Questions</i> (e.g., <i>Would you like a house or a flat?</i>).</p>



				</div>


				<div id="semRelations">
					<h1 style="text-align: center">Semantic Relations used in Knowledge
						Parser</h1>
					<p>
						The semantic relation labels in the output of K-Parser are inspired
						from <a href="http://www.cs.utexas.edu/users/mfkb/RKF/tree/">Knowledge
							Machine</a> ontology<!-- , <a href="http://amr.isi.edu/index.html">Abstract
							Meaning Representation</a> --> and also many newly created relations. A
						categorized list of all the semantic relations used is mentioned
						below.
					</p>
					<h3 style="text-align: left">
						Relation labels from <a
							href="http://www.cs.utexas.edu/users/mfkb/RKF/tree/">Knowledge
							Machine</a> ontology
					</h3>
					<table style="float: left" border="1">
						<tr>
							<td><h4 style="text-align: left">
									<b>Event-Event Relations</b>
								</h4></td>
							<td><h4 style="text-align: left">
									<b>Event-Entity Relations</b>
								</h4></td>
							<td><h4 style="text-align: left">
									<b>Entity-Entity Relations</b>
								</h4></td>
							<td><h4 style="text-align: left">
									<b>Entity-Value Relations</b>
								</h4></td>
							<td><h4 style="text-align: left">
									<b>Other Relations</b>
								</h4></td>
						</tr>
						<tr>
							<td>

								<ul>
									<li>objective
									<li>previous_event
									<li>next_event
									<li>caused_by
									<li>causes
									<li>inhibited_by
								</ul>
							</td>
							<td>

								<ul>
									<li>agent
									<li>recipient
									<li>object
									<li>destination/location
									<li>destination
									<li>destination/time_at
									<li>location
									<li>site/location/time_at
									<li>beneficiary
									<li>raw_material
									<li>origin
								</ul>
							</td>
							<td>

								<ul>
									<li>is_possessed_by
									<li>has_part
									<li>complement
								</ul>
							</td>
							<td>

								<ul>
									<li>trait
									<li>size
								</ul>
							</td>
							<td>

								<ul>
									<li>information_content
								</ul>
							</td>
						</tr>
					</table>

					<table class="table1" style="float:left">
					<tr>
					<td><h3 style="text-align: left">
									Newly created relation labels
								</h3>
								</td>
					</tr>
					</table>
					
					<table style="float:left" border="1">
						<tr>
							<td><h4 style="text-align: center">
									<b>Event-Event Relations</b>
								</h4></td>
							<td><h4 style="text-align: center">
									<b>Event-Entity Relations</b>
								</h4></td>
							<td><h4 style="text-align: center">
									<b>Entity-Entity Relations</b>
								</h4></td>
							<td><h4 style="text-align: center">
									<b>Other Relations</b>
								</h4></td>
						</tr>
						<tr>
							<td>
								<ul>
									<li>subject</li>
									<li>passive_subject</li>
								</ul>
							</td>
							<td>
								<ul>
									<li>verb_compliment</li>
									<li>supporting_verb</li>
									<li>modal_verb</li>
									<li>passive_supporting_verb</li>
									<li>time</li>
									<li>modifier</li>
									<li>accompanier</li>
								</ul>
							</td>
							<td>

								<ul>
									<li>is_related_to
									<li>in_conjunction_with
									<li>in_disjunction_with
									<li>identifier
									<li>referent
								</ul>
							</td>
							<td>
								<ul>
									<li>negative
									<li>dependent
									<li>punctuation
									<li>quantity_modifier
								</ul>
							</td>
						</tr>
					</table>





				
					<table class="table1"  style="float: left">
					<tr>
							<td>
								<h3 style="text-align: left">Relation labels from
									Preposition Sense Disambiguation</h3>
									<p> In our approach we have used a multilayer perceptron classifier to 
					classify the preposition senses into a set of senses from KM. We used 
					<a href="http://www.clres.com/prepositions.html">The Preposition Project</a> corpus as the 
					training corpus for it and manually annotated the training sentences with
					 appropriate semantic labels from KM library and new relations as required.	A list of all the semantic relations used for prepositions
					 is shown in the table below.				
					</p>
						</td>
					</tr>
					</table>
					
					
					<table style="float:left" border = "1" width="50%">
						<tr>
							<td><b>Preposition</b></td>
							<td><b>Semantic Relation</b></td>
						</tr>
						<tr>
							<td rowspan="3">about</td>
						</tr>
						<tr>
							<td>is_around</td>
						</tr>
						<tr>
							<td>location_of</td>
						</tr>
						<tr>
							<td rowspan="3">above</td>
						</tr>
						<tr>
							<td>superior_status</td>
						</tr>
						<tr>
							<td>is_above</td>
						</tr>
						<tr>
							<td rowspan="5">after</td>
						</tr>
						<tr>
							<td>is_following</td>
						</tr>
						<tr>
							<td>time_after</td>
						</tr>
						<tr>
							<td>based_on</td>
						</tr>
						<tr>
							<td>inferior_status</td>
						</tr>
						<tr>
							<td rowspan="2">against</td>
						</tr>
						<tr>
							<td>in_opposition</td>
						</tr>
						<tr>
							<td rowspan="2">along</td>
						</tr>
						<tr>
							<td>with_adherence</td>
						</tr>
						<tr>
							<td rowspan="5">at</td>
						</tr>
						<tr>
							<td>rate</td>
						</tr>
						<tr>
							<td>toward</td>
						</tr>
						<tr>
							<td>time_of_existence</td>
						</tr>
						<tr>
							<td>resulting_state</td>
						</tr>
						<tr>
							<td rowspan="4">before</td>
						</tr>
						<tr>
							<td>in_front_of</td>
						</tr>
						<tr>
							<td>is_before_location</td>
						</tr>
						<tr>
							<td>is_before_time</td>
						</tr>
						<tr>
							<td rowspan="3">behind</td>
						</tr>
						<tr>
							<td>is_behind_development</td>
						</tr>
						<tr>
							<td>is_behind_location</td>
						</tr>
						<tr>
							<td rowspan="2">beneath</td>
						</tr>
						<tr>
							<td>is_under_location</td>
						</tr>
						<tr>
							<td rowspan="4">between</td>
						</tr>
						<tr>
							<td>interval_between</td>
						</tr>
						<tr>
							<td>reference_between</td>
						</tr>
						<tr>
							<td>time_between</td>
						</tr>
						<tr>
							<td rowspan="8">by</td>
						</tr>
						<tr>
							<td>according_to</td>
						</tr>
						<tr>
							<td>distance</td>
						</tr>
						<tr>
							<td>by_means_of</td>
						</tr>
						<tr>
							<td>owner</td>
						</tr>
						<tr>
							<td>frequency</td>
						</tr>
						<tr>
							<td>instrument</td>
						</tr>
						<tr>
							<td>time_ends</td>
						</tr>
						<tr>
							<td rowspan="2">down</td>
						</tr>
						<tr>
							<td>along</td>
						</tr>
						<tr>
							<td rowspan="8">for</td>
						</tr>
						<tr>
							<td>result</td>
						</tr>
						<tr>
							<td>is_goal_of</td>
						</tr>
						<tr>
							<td>benefactive</td>
						</tr>
						<tr>
							<td>with_respect_to</td>
						</tr>
						<tr>
							<td>employer</td>
						</tr>
						<tr>
							<td>in_exchange_of</td>
						</tr>
						<tr>
							<td>relative_to</td>
						</tr>
						<tr>
							<td rowspan="9">from</td>
						</tr>
						<tr>
							<td>origin_time</td>
						</tr>
						<tr>
							<td>origin_location</td>
						</tr>
						<tr>
							<td>compared_to</td>
						</tr>
						<tr>
							<td>origin_value</td>
						</tr>
						<tr>
							<td>source</td>
						</tr>
						<tr>
							<td>performer</td>
						</tr>
						<tr>
							<td>seperate_from</td>
						</tr>
						<tr>
							<td>state_changed_from</td>
						</tr>
					</table>



					<table style="float: left" border="1" width="50%">
						<tr>
							<td><b>Preposition</b></td>
							<td><b>Semantic Relation</b></td>
						</tr>
						<tr>
							<td rowspan="7">in</td>
						</tr>
						<tr>
							<td>is_part_of</td>
						</tr>
						<tr>
							<td>is_inside_location</td>
						</tr>
						<tr>
							<td>aspect</td>
						</tr>
						<tr>
							<td>is_participant_in</td>
						</tr>
						<tr>
							<td>clothed_in</td>
						</tr>
						<tr>
							<td>has_style</td>
						</tr>
						<tr>
							<td rowspan="2">inside</td>
						</tr>
						<tr>
							<td>is_inside_emotion</td>
						</tr>
						<tr>
							<td rowspan="4">into</td>
						</tr>
						<tr>
							<td>topic</td>
						</tr>
						<tr>
							<td>object_of_contact</td>
						</tr>
						<tr>
							<td>modified_to</td>
						</tr>
						<tr>
							<td rowspan="3">like</td>
						</tr>
						<tr>
							<td>manner_of</td>
						</tr>
						<tr>
							<td>has_element</td>
						</tr>
						<tr>
							<td rowspan="10">of</td>
						</tr>
						<tr>
							<td>experiencer</td>
						</tr>
						<tr>
							<td>trait_of</td>
						</tr>
						<tr>
							<td>motivated_by</td>
						</tr>
						<tr>
							<td>value</td>
						</tr>
						<tr>
							<td>type_of</td>
						</tr>
						<tr>
							<td>constitutes</td>
						</tr>
						<tr>
							<td>container_of</td>
						</tr>
						<tr>
							<td>regarding</td>
						</tr>
						<tr>
							<td>product_of</td>
						</tr>
						<tr>
							<td rowspan="4">off</td>
						</tr>
						<tr>
							<td>away_from</td>
						</tr>
						<tr>
							<td>away_from_location</td>
						</tr>
						<tr>
							<td>extending_to</td>
						</tr>
						<tr>
							<td rowspan="9">on</td>
						</tr>
						<tr>
							<td>in_course_of</td>
						</tr>
						<tr>
							<td>supported_by</td>
						</tr>
						<tr>
							<td>is_on</td>
						</tr>
						<tr>
							<td>target</td>
						</tr>
						<tr>
							<td>base</td>
						</tr>
						<tr>
							<td>has_role</td>
						</tr>
						<tr>
							<td>medium</td>
						</tr>
						<tr>
							<td>mode</td>
						</tr>
						<tr>
							<td rowspan="2">onto</td>
						</tr>
						<tr>
							<td>onto</td>
						</tr>
						<tr>
							<td rowspan="6">over</td>
						</tr>
						<tr>
							<td>on_top_of</td>
						</tr>
						<tr>
							<td>extended_over_location</td>
						</tr>
						<tr>
							<td>on_top_of_location</td>
						</tr>
						<tr>
							<td>is_over_location</td>
						</tr>
						<tr>
							<td>is_opposite_location</td>
						</tr>
						<tr>
							<td rowspan="4">through</td>
						</tr>
						<tr>
							<td>containing_entity</td>
						</tr>
						<tr>
							<td>path</td>
						</tr>
						<tr>
							<td>is_across_location</td>
						</tr>
						<tr>
							<td rowspan="3">to</td>
						</tr>
						<tr>
							<td>object_of_interest</td>
						</tr>
						<tr>
							<td>attachment</td>
						</tr>
						<tr>
							<td rowspan="3">towards</td>
						</tr>
						<tr>
							<td>direction</td>
						</tr>
						<tr>
							<td>approaching</td>
						</tr>
						<tr>
							<td rowspan="2">with</td>
						</tr>
						<tr>
							<td>has_entity</td>
						</tr>
					</table>

				</div>


				<div id="adjectives">

					<table class="table1" style="float: left" width="100%" border="0">
						<tr>
							<td>
								<h1>List of Adjectives and their conceptual classes</h1> We used WordNet lexicon senses to get the conceptual classes of the
								words in the input sentence. These classes were not descriptive enough for representing the 
								classes of adjective words. So, we identified most widely used adjectives and annotated them with their conceptual classes.
								The annotated list of adjectives is shown in the table below.
							</td>
						</tr>
					</table>

					<table border="1">
						<tr>
							<td><b>ADJECTIVE</b></td>
							<td><b>CONCEPTUAL CLASS</b></td>
							<td><b>ADJECTIVE</b></td>
							<td><b>CONCEPTUAL CLASS</b></td>
							<td><b>ADJECTIVE</b></td>
							<td><b>CONCEPTUAL CLASS</b></td>
						</tr>
						<tr>
							<td>brave</td>
							<td>quality_descriptive</td>
							<td>light blue</td>
							<td>color_descriptive</td>
							<td>plastic</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>bue-green</td>
							<td>color_descriptive</td>
							<td>strapping</td>
							<td>size_descriptive</td>
							<td>bright</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>bumpy</td>
							<td>touch_descriptive</td>
							<td>uneven</td>
							<td>touch_descriptive</td>
							<td>cinnabar</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>lukewarm</td>
							<td>touch_descriptive</td>
							<td>international orange</td>
							<td>color_descriptive</td>
							<td>even-tempered</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>apprehensive</td>
							<td>quality_descriptive</td>
							<td>tactful</td>
							<td>quality_descriptive</td>
							<td>quarrelsome</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>olive</td>
							<td>color_descriptive</td>
							<td>pint-size</td>
							<td>size_descriptive</td>
							<td>army green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>petulant</td>
							<td>quality_descriptive</td>
							<td>exacting</td>
							<td>quality_descriptive</td>
							<td>persian-ect</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>hungry</td>
							<td>negative-feeling_descriptive</td>
							<td>quizzical</td>
							<td>negative-tone_descriptive</td>
							<td>thistle</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>oversize</td>
							<td>size_descriptive</td>
							<td>wide</td>
							<td>size_descriptive</td>
							<td>assertive</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>enthusiastic</td>
							<td>quality_descriptive</td>
							<td>mint green</td>
							<td>color_descriptive</td>
							<td>vigilant</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>sloppy</td>
							<td>quality_descriptive</td>
							<td>alice blue</td>
							<td>color_descriptive</td>
							<td>immodest</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>bitter</td>
							<td>negative-tone_descriptive</td>
							<td>sick</td>
							<td>negative-emotion_descriptive</td>
							<td>each</td>
							<td>indefinite</td>
						</tr>
						<tr>
							<td>big</td>
							<td>size_descriptive</td>
							<td>steady</td>
							<td>quality_descriptive</td>
							<td>linen</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>smooth</td>
							<td>touch_descriptive</td>
							<td>naughty</td>
							<td>quality_descriptive</td>
							<td>spiteful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>dependent</td>
							<td>quality_descriptive</td>
							<td>purposeful</td>
							<td>negative-tone_descriptive</td>
							<td>mulberry</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>impractical</td>
							<td>quality_descriptive</td>
							<td>lion-yellow</td>
							<td>color_descriptive</td>
							<td>patient</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>caustic</td>
							<td>quality_descriptive</td>
							<td>bright blue</td>
							<td>color_descriptive</td>
							<td>grey</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>interesting</td>
							<td>quality_descriptive</td>
							<td>coherent</td>
							<td>quality_descriptive</td>
							<td>longing</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>sophisticated</td>
							<td>quality_descriptive</td>
							<td>powder blue</td>
							<td>color_descriptive</td>
							<td>ridiculous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>persian green</td>
							<td>color_descriptive</td>
							<td>moronic</td>
							<td>quality_descriptive</td>
							<td>myrtle</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>purple</td>
							<td>color_descriptive</td>
							<td>confused</td>
							<td>quality_descriptive</td>
							<td>sandy brown</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>fandango -purple</td>
							<td>color_descriptive</td>
							<td>taken aback</td>
							<td>negative-feeling_descriptive</td>
							<td>red</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>orchid</td>
							<td>color_descriptive</td>
							<td>picky</td>
							<td>quality_descriptive</td>
							<td>hesitant</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>discreet</td>
							<td>quality_descriptive</td>
							<td>alarmed</td>
							<td>negative-tone_descriptive</td>
							<td>reluctant</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>competent</td>
							<td>quality_descriptive</td>
							<td>insensitive</td>
							<td>quality_descriptive</td>
							<td>compact</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>pessimistic</td>
							<td>negative-feeling_descriptive</td>
							<td>condescending</td>
							<td>quality_descriptive</td>
							<td>affectionate</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>provocative</td>
							<td>quality_descriptive</td>
							<td>guilt</td>
							<td>negative-emotion_descriptive</td>
							<td>french rose</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>lilac</td>
							<td>color_descriptive</td>
							<td>welsh</td>
							<td>origin_descriptive</td>
							<td>pumpkin</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>fat</td>
							<td>size_descriptive</td>
							<td>scared</td>
							<td>negative-feeling_descriptive</td>
							<td>churlish</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>timid</td>
							<td>quality_descriptive</td>
							<td>lean</td>
							<td>size_descriptive</td>
							<td>involved</td>
							<td>positive-tone_descriptive</td>
						</tr>
						<tr>
							<td>delicate</td>
							<td>quality_descriptive</td>
							<td>brass</td>
							<td>color_descriptive</td>
							<td>fast</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>religious</td>
							<td>tone_descriptive</td>
							<td>large</td>
							<td>size_descriptive</td>
							<td>hope</td>
							<td>positive-emotion_descriptive</td>
						</tr>
						<tr>
							<td>hating</td>
							<td>negative-tone_descriptive</td>
							<td>hot magenta</td>
							<td>color_descriptive</td>
							<td>bistre</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>distant</td>
							<td>quality_descriptive</td>
							<td>mÄori</td>
							<td>origin_descriptive</td>
							<td>prejudiced</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>periwinkle</td>
							<td>color_descriptive</td>
							<td>surly</td>
							<td>quality_descriptive</td>
							<td>generous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>dayak</td>
							<td>origin_descriptive</td>
							<td>cautious</td>
							<td>quality_descriptive</td>
							<td>respectful</td>
							<td>positive-emotion_descriptive</td>
						</tr>
						<tr>
							<td>red-violet</td>
							<td>color_descriptive</td>
							<td>tall</td>
							<td>size_descriptive</td>
							<td>expansive</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>amiable</td>
							<td>quality_descriptive</td>
							<td>peach</td>
							<td>color_descriptive</td>
							<td>purple taupe</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>vermilion</td>
							<td>color_descriptive</td>
							<td>cubical</td>
							<td>shape_descriptive</td>
							<td>great</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>wooden</td>
							<td>touch_descriptive</td>
							<td>obnoxious</td>
							<td>negative-tone_descriptive</td>
							<td>enterprising</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>brown</td>
							<td>color_descriptive</td>
							<td>eternal</td>
							<td>age_descriptive</td>
							<td>meager</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>resentful</td>
							<td>negative-tone_descriptive</td>
							<td>suspicious</td>
							<td>quality_descriptive</td>
							<td>tomato</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>papua</td>
							<td>origin_descriptive</td>
							<td>medium blue</td>
							<td>color_descriptive</td>
							<td>gamboge</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>awesome</td>
							<td>quality_descriptive</td>
							<td>prickly</td>
							<td>touch_descriptive</td>
							<td>burnt orange</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>social</td>
							<td>tone_descriptive</td>
							<td>slushy</td>
							<td>touch_descriptive</td>
							<td>watchful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>ukrainian</td>
							<td>origin_descriptive</td>
							<td>indigo</td>
							<td>color_descriptive</td>
							<td>spanish</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>old lavender</td>
							<td>color_descriptive</td>
							<td>alabaster</td>
							<td>color_descriptive</td>
							<td>secretive</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>flabby</td>
							<td>quality_descriptive</td>
							<td>sensible</td>
							<td>quality_descriptive</td>
							<td>rose</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>unhelpful</td>
							<td>quality_descriptive</td>
							<td>electric indigo</td>
							<td>color_descriptive</td>
							<td>amusing</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>lime green</td>
							<td>color_descriptive</td>
							<td>gargantuan</td>
							<td>size_descriptive</td>
							<td>pastel-ect</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>playful</td>
							<td>positive-feeling_descriptive</td>
							<td>broad</td>
							<td>size_descriptive</td>
							<td>nosy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cruel</td>
							<td>quality_descriptive</td>
							<td>short</td>
							<td>size_descriptive</td>
							<td>yellow-green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>lovable</td>
							<td>quality_descriptive</td>
							<td>testy</td>
							<td>quality_descriptive</td>
							<td>waxen</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>iridescent-ect</td>
							<td>color_descriptive</td>
							<td>clean</td>
							<td>quality_descriptive</td>
							<td>endless</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>hot pink</td>
							<td>color_descriptive</td>
							<td>dodge blue</td>
							<td>color_descriptive</td>
							<td>steel blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>speedy</td>
							<td>quality_descriptive</td>
							<td>peaceful</td>
							<td>quality_descriptive</td>
							<td>tearful</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>greek</td>
							<td>origin_descriptive</td>
							<td>faith</td>
							<td>positive-emotion_descriptive</td>
							<td>green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>beautiful</td>
							<td>quality_descriptive</td>
							<td>enormous</td>
							<td>size_descriptive</td>
							<td>sedate</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>reserved</td>
							<td>quality_descriptive</td>
							<td>morose</td>
							<td>quality_descriptive</td>
							<td>drowsy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cyan</td>
							<td>color_descriptive</td>
							<td>hollow</td>
							<td>shape_descriptive</td>
							<td>cosmic latte</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>square</td>
							<td>shape_descriptive</td>
							<td>unimaginative</td>
							<td>quality_descriptive</td>
							<td>papuan</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>caput mortuum</td>
							<td>color_descriptive</td>
							<td>short-tempered</td>
							<td>quality_descriptive</td>
							<td>polite</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>devastated</td>
							<td>negative-emotion_descriptive</td>
							<td>mustard</td>
							<td>color_descriptive</td>
							<td>animated</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>envy</td>
							<td>negative-emotion_descriptive</td>
							<td>hebrew</td>
							<td>origin_descriptive</td>
							<td>sassy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>every</td>
							<td>indefinite</td>
							<td>sweaty</td>
							<td>touch_descriptive</td>
							<td>heliotrope</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>olive drab</td>
							<td>color_descriptive</td>
							<td>motivated</td>
							<td>quality_descriptive</td>
							<td>gentle</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>warmhearted</td>
							<td>quality_descriptive</td>
							<td>sleepy</td>
							<td>quality_descriptive</td>
							<td>kelly green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>maya blue</td>
							<td>color_descriptive</td>
							<td>fastidious</td>
							<td>quality_descriptive</td>
							<td>hindi</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>prussian blue</td>
							<td>color_descriptive</td>
							<td>teal</td>
							<td>color_descriptive</td>
							<td>maternal</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>depressed</td>
							<td>negative-emotion_descriptive</td>
							<td>one-sided</td>
							<td>quality_descriptive</td>
							<td>strong</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>dark brown</td>
							<td>color_descriptive</td>
							<td>restless</td>
							<td>quality_descriptive</td>
							<td>cranky</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>polish</td>
							<td>origin_descriptive</td>
							<td>midnight blue</td>
							<td>color_descriptive</td>
							<td>tiny</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>viridian</td>
							<td>color_descriptive</td>
							<td>coral</td>
							<td>color_descriptive</td>
							<td>african</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>startled</td>
							<td>negative-tone_descriptive</td>
							<td>quick</td>
							<td>quality_descriptive</td>
							<td>sodden</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>volcanic</td>
							<td>quality_descriptive</td>
							<td>lavender-ect</td>
							<td>color_descriptive</td>
							<td>undersized</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>gorgeous</td>
							<td>positive-feeling_descriptive</td>
							<td>creme</td>
							<td>color_descriptive</td>
							<td>flashy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>conceited</td>
							<td>quality_descriptive</td>
							<td>measly</td>
							<td>size_descriptive</td>
							<td>ecru</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>annual</td>
							<td>time_descriptive</td>
							<td>snobby</td>
							<td>quality_descriptive</td>
							<td>political</td>
							<td>tone_descriptive</td>
						</tr>
						<tr>
							<td>gooey</td>
							<td>touch_descriptive</td>
							<td>full-size</td>
							<td>size_descriptive</td>
							<td>indian</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>scottish</td>
							<td>origin_descriptive</td>
							<td>embarrassed</td>
							<td>negative-feeling_descriptive</td>
							<td>daily</td>
							<td>time_descriptive</td>
						</tr>
						<tr>
							<td>greasy</td>
							<td>touch_descriptive</td>
							<td>balanced</td>
							<td>quality_descriptive</td>
							<td>old gold</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>thoughtless</td>
							<td>quality_descriptive</td>
							<td>amber</td>
							<td>color_descriptive</td>
							<td>lithe</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>tender</td>
							<td>touch_descriptive</td>
							<td>lavender blue</td>
							<td>color_descriptive</td>
							<td>proud</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>callous</td>
							<td>quality_descriptive</td>
							<td>curved</td>
							<td>shape_descriptive</td>
							<td>pastel pink</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>amethyst</td>
							<td>color_descriptive</td>
							<td>impulsive</td>
							<td>quality_descriptive</td>
							<td>nice</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>blunt</td>
							<td>quality_descriptive</td>
							<td>yellow</td>
							<td>color_descriptive</td>
							<td>plain-speaking</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>unemotional</td>
							<td>quality_descriptive</td>
							<td>tyrain purple</td>
							<td>color_descriptive</td>
							<td>pensive</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>lovely</td>
							<td>quality_descriptive</td>
							<td>stinging</td>
							<td>touch_descriptive</td>
							<td>hearty</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cheerful</td>
							<td>quality_descriptive</td>
							<td>cool</td>
							<td>touch_descriptive</td>
							<td>balinese</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>sour</td>
							<td>quality_descriptive</td>
							<td>attentive</td>
							<td>quality_descriptive</td>
							<td>dark-ect</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>republican</td>
							<td>tone_descriptive</td>
							<td>swedish</td>
							<td>origin_descriptive</td>
							<td>waiting</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>hysterical</td>
							<td>quality_descriptive</td>
							<td>inconsiderate</td>
							<td>quality_descriptive</td>
							<td>grouchy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>appreciative</td>
							<td>positive-emotion_descriptive</td>
							<td>quiet</td>
							<td>quality_descriptive</td>
							<td>han purple</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>electric blue</td>
							<td>color_descriptive</td>
							<td>sizable</td>
							<td>size_descriptive</td>
							<td>tubby</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>serbo-croatian</td>
							<td>origin_descriptive</td>
							<td>thickset</td>
							<td>size_descriptive</td>
							<td>terrible</td>
							<td>negative-feeling_descriptive</td>
						</tr>
						<tr>
							<td>wisteria</td>
							<td>color_descriptive</td>
							<td>denim</td>
							<td>color_descriptive</td>
							<td>islamic green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>ugly</td>
							<td>quality_descriptive</td>
							<td>brawny</td>
							<td>size_descriptive</td>
							<td>demonic</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>delightful</td>
							<td>quality_descriptive</td>
							<td>tenne</td>
							<td>color_descriptive</td>
							<td>lively</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>hans - blue</td>
							<td>color_descriptive</td>
							<td>drunk</td>
							<td>quality_descriptive</td>
							<td>unlimited</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>blue-violet</td>
							<td>color_descriptive</td>
							<td>american</td>
							<td>origin_descriptive</td>
							<td>papaya whip</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>turkic</td>
							<td>origin_descriptive</td>
							<td>limitless</td>
							<td>size_descriptive</td>
							<td>proficient</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>gold</td>
							<td>color_descriptive</td>
							<td>lusting</td>
							<td>positive-emotion_descriptive</td>
							<td>baby</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>sensitive</td>
							<td>quality_descriptive</td>
							<td>burnt sienna</td>
							<td>color_descriptive</td>
							<td>sentimental</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>yale blue</td>
							<td>color_descriptive</td>
							<td>cosmic</td>
							<td>size_descriptive</td>
							<td>shamrock green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>young</td>
							<td>quality_descriptive</td>
							<td>teeny</td>
							<td>size_descriptive</td>
							<td>fleshy</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>narrow</td>
							<td>size_descriptive</td>
							<td>sticky</td>
							<td>touch_descriptive</td>
							<td>mercurial</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>thirsty</td>
							<td>negative-feeling_descriptive</td>
							<td>honeydew- yellow</td>
							<td>color_descriptive</td>
							<td>towering</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>medium carmine</td>
							<td>color_descriptive</td>
							<td>pale carmine</td>
							<td>color_descriptive</td>
							<td>dextrous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>good</td>
							<td>quality_descriptive</td>
							<td>crass</td>
							<td>quality_descriptive</td>
							<td>unaffected</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>byzantium- purple</td>
							<td>color_descriptive</td>
							<td>curious</td>
							<td>quality_descriptive</td>
							<td>unwilling</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>practical</td>
							<td>quality_descriptive</td>
							<td>chic</td>
							<td>quality_descriptive</td>
							<td>light pink</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>conical</td>
							<td>shape_descriptive</td>
							<td>dutch</td>
							<td>origin_descriptive</td>
							<td>iridescent pink</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>orange</td>
							<td>color_descriptive</td>
							<td>prudent</td>
							<td>quality_descriptive</td>
							<td>teeny-weeny</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>incompetent</td>
							<td>quality_descriptive</td>
							<td>beneficent</td>
							<td>quality_descriptive</td>
							<td>resourceful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>liking</td>
							<td>positive-emotion_descriptive</td>
							<td>sneering</td>
							<td>quality_descriptive</td>
							<td>sweet</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>several</td>
							<td>indefinite</td>
							<td>early</td>
							<td>time_descriptive</td>
							<td>deep peach</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>australian</td>
							<td>origin_descriptive</td>
							<td>pudgy</td>
							<td>size_descriptive</td>
							<td>empowered</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>corn</td>
							<td>color_descriptive</td>
							<td>joyous</td>
							<td>quality_descriptive</td>
							<td>drugged</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>vast</td>
							<td>size_descriptive</td>
							<td>dirty</td>
							<td>quality_descriptive</td>
							<td>querulous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>unfriendly</td>
							<td>quality_descriptive</td>
							<td>uncooperative</td>
							<td>quality_descriptive</td>
							<td>well-developed</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>hefty</td>
							<td>size_descriptive</td>
							<td>engrossed</td>
							<td>positive-feeling_descriptive</td>
							<td>distrustful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>regular</td>
							<td>time_descriptive</td>
							<td>corpulent</td>
							<td>size_descriptive</td>
							<td>outsized</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>korean</td>
							<td>origin_descriptive</td>
							<td>curvy</td>
							<td>size_descriptive</td>
							<td>sky blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>cubby</td>
							<td>size_descriptive</td>
							<td>democratic</td>
							<td>tone_descriptive</td>
							<td>artistic</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>intolerant</td>
							<td>quality_descriptive</td>
							<td>weak</td>
							<td>quality_descriptive</td>
							<td>historical</td>
							<td>age_descriptive</td>
						</tr>
						<tr>
							<td>gloomy</td>
							<td>quality_descriptive</td>
							<td>giant</td>
							<td>size_descriptive</td>
							<td>rectangular</td>
							<td>shape_descriptive</td>
						</tr>
						<tr>
							<td>navajo white</td>
							<td>color_descriptive</td>
							<td>beefy</td>
							<td>size_descriptive</td>
							<td>sad</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>amazed</td>
							<td>positive-feeling_descriptive</td>
							<td>jovial</td>
							<td>quality_descriptive</td>
							<td>outgoing</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>modest</td>
							<td>quality_descriptive</td>
							<td>calculated</td>
							<td>negative-tone_descriptive</td>
							<td>pale chestnut</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>fervent</td>
							<td>quality_descriptive</td>
							<td>jocular</td>
							<td>quality_descriptive</td>
							<td>mild</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>pink</td>
							<td>color_descriptive</td>
							<td>attracted</td>
							<td>positive-feeling_descriptive</td>
							<td>cinnamon</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>pine green</td>
							<td>color_descriptive</td>
							<td>gritty</td>
							<td>touch_descriptive</td>
							<td>slender</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>pink-orange</td>
							<td>color_descriptive</td>
							<td>hungarian</td>
							<td>origin_descriptive</td>
							<td>dusty</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>ultramarine</td>
							<td>color_descriptive</td>
							<td>huge</td>
							<td>size_descriptive</td>
							<td>meticulous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>solid</td>
							<td>shape_descriptive</td>
							<td>logical</td>
							<td>quality_descriptive</td>
							<td>jolly</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>buff</td>
							<td>color_descriptive</td>
							<td>aqua</td>
							<td>color_descriptive</td>
							<td>some</td>
							<td>indefinite</td>
						</tr>
						<tr>
							<td>lemon</td>
							<td>color_descriptive</td>
							<td>grey-asparagus</td>
							<td>color_descriptive</td>
							<td>judgmental</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>unreliable</td>
							<td>quality_descriptive</td>
							<td>well-intentioned</td>
							<td>quality_descriptive</td>
							<td>scarlet</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>coral red</td>
							<td>color_descriptive</td>
							<td>fortunate</td>
							<td>positive-feeling_descriptive</td>
							<td>rolay purple</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>foolish</td>
							<td>negative-tone_descriptive</td>
							<td>tranquil</td>
							<td>quality_descriptive</td>
							<td>sardonic</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>demure</td>
							<td>quality_descriptive</td>
							<td>industrious</td>
							<td>quality_descriptive</td>
							<td>slothful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>united nations blue</td>
							<td>color_descriptive</td>
							<td>firebrick</td>
							<td>color_descriptive</td>
							<td>indian red</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>unsophisticated</td>
							<td>quality_descriptive</td>
							<td>romani</td>
							<td>origin_descriptive</td>
							<td>resigned</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>despise</td>
							<td>negative-emotion_descriptive</td>
							<td>mini</td>
							<td>size_descriptive</td>
							<td>uncertain</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>friendly</td>
							<td>quality_descriptive</td>
							<td>colbalt</td>
							<td>color_descriptive</td>
							<td>excited</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>navy blue</td>
							<td>color_descriptive</td>
							<td>persian blue</td>
							<td>color_descriptive</td>
							<td>remorseful</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>soulful</td>
							<td>quality_descriptive</td>
							<td>level-headed</td>
							<td>quality_descriptive</td>
							<td>fuchsia</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>burgandy</td>
							<td>color_descriptive</td>
							<td>swift</td>
							<td>quality_descriptive</td>
							<td>gaulish</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>annoyed</td>
							<td>negative-tone_descriptive</td>
							<td>skimpy</td>
							<td>size_descriptive</td>
							<td>italian</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>lethargic</td>
							<td>quality_descriptive</td>
							<td>persian</td>
							<td>origin_descriptive</td>
							<td>overcome</td>
							<td>positive-emotion_descriptive</td>
						</tr>
						<tr>
							<td>reassuring</td>
							<td>quality_descriptive</td>
							<td>vikings</td>
							<td>origin_descriptive</td>
							<td>unpopular</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cold</td>
							<td>touch_descriptive</td>
							<td>zinnwaldite</td>
							<td>color_descriptive</td>
							<td>yearly</td>
							<td>time_descriptive</td>
						</tr>
						<tr>
							<td>nasty</td>
							<td>quality_descriptive</td>
							<td>romanian</td>
							<td>origin_descriptive</td>
							<td>narcissistic</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>candid</td>
							<td>quality_descriptive</td>
							<td>stable</td>
							<td>quality_descriptive</td>
							<td>rotund</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>orange red</td>
							<td>color_descriptive</td>
							<td>sullen</td>
							<td>quality_descriptive</td>
							<td>horrified</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>tea green</td>
							<td>color_descriptive</td>
							<td>crimson</td>
							<td>color_descriptive</td>
							<td>thick</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>serene</td>
							<td>quality_descriptive</td>
							<td>seashell</td>
							<td>color_descriptive</td>
							<td>moody</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>columbia blue</td>
							<td>color_descriptive</td>
							<td>whopping</td>
							<td>size_descriptive</td>
							<td>calm</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>stoic</td>
							<td>quality_descriptive</td>
							<td>finnish</td>
							<td>origin_descriptive</td>
							<td>passionate</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>ferocious</td>
							<td>quality_descriptive</td>
							<td>old norse</td>
							<td>origin_descriptive</td>
							<td>winter grey</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>cerise</td>
							<td>color_descriptive</td>
							<td>orderly</td>
							<td>quality_descriptive</td>
							<td>egyptian blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>believing</td>
							<td>positive-tone_descriptive</td>
							<td>noisy</td>
							<td>quality_descriptive</td>
							<td>dramatic</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>orange peel</td>
							<td>color_descriptive</td>
							<td>abnormal</td>
							<td>quality_descriptive</td>
							<td>crooked</td>
							<td>shape_descriptive</td>
						</tr>
						<tr>
							<td>dutiful</td>
							<td>quality_descriptive</td>
							<td>indefatigable</td>
							<td>quality_descriptive</td>
							<td>soft</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>scrawny</td>
							<td>size_descriptive</td>
							<td>downy</td>
							<td>touch_descriptive</td>
							<td>average</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>reliable</td>
							<td>quality_descriptive</td>
							<td>enlightened</td>
							<td>positive-tone_descriptive</td>
							<td>sadistic</td>
							<td>negative-feeling_descriptive</td>
						</tr>
						<tr>
							<td>outspoken</td>
							<td>quality_descriptive</td>
							<td>apricot</td>
							<td>color_descriptive</td>
							<td>self-pity</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>free</td>
							<td>positive-feeling_descriptive</td>
							<td>childish</td>
							<td>quality_descriptive</td>
							<td>staid</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>paltry</td>
							<td>size_descriptive</td>
							<td>slippery</td>
							<td>touch_descriptive</td>
							<td>slow</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>undependable</td>
							<td>quality_descriptive</td>
							<td>deep</td>
							<td>quality_descriptive</td>
							<td>deferential</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>black</td>
							<td>color_descriptive</td>
							<td>saffron</td>
							<td>color_descriptive</td>
							<td>sea green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>purple-blue</td>
							<td>color_descriptive</td>
							<td>vulnerable</td>
							<td>quality_descriptive</td>
							<td>moss green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>quartz</td>
							<td>color_descriptive</td>
							<td>salmon</td>
							<td>color_descriptive</td>
							<td>dainty</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>verdigris- green</td>
							<td>color_descriptive</td>
							<td>artificial</td>
							<td>quality_descriptive</td>
							<td>carrot orange</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>deep fuchsia</td>
							<td>color_descriptive</td>
							<td>immense</td>
							<td>size_descriptive</td>
							<td>colossal</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>agreeable</td>
							<td>quality_descriptive</td>
							<td>excellent</td>
							<td>quality_descriptive</td>
							<td>raw umber</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>spirited</td>
							<td>quality_descriptive</td>
							<td>naive</td>
							<td>quality_descriptive</td>
							<td>distraught</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>extensive</td>
							<td>size_descriptive</td>
							<td>emaciated</td>
							<td>size_descriptive</td>
							<td>unguarded</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>supercilious</td>
							<td>quality_descriptive</td>
							<td>mad</td>
							<td>negative-emotion_descriptive</td>
							<td>south african</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>wet</td>
							<td>touch_descriptive</td>
							<td>obese</td>
							<td>size_descriptive</td>
							<td>pansy- purple</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>anxiety</td>
							<td>negative-emotion_descriptive</td>
							<td>wee</td>
							<td>size_descriptive</td>
							<td>shy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>amazing</td>
							<td>quality_descriptive</td>
							<td>independent</td>
							<td>quality_descriptive</td>
							<td>chocolate</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>overjoyed</td>
							<td>positive-emotion_descriptive</td>
							<td>fluffy</td>
							<td>touch_descriptive</td>
							<td>titanic</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>lazy</td>
							<td>quality_descriptive</td>
							<td>clever</td>
							<td>quality_descriptive</td>
							<td>russet</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>handsome</td>
							<td>positive-feeling_descriptive</td>
							<td>disagreeable</td>
							<td>quality_descriptive</td>
							<td>pear</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>sami</td>
							<td>origin_descriptive</td>
							<td>irritating</td>
							<td>quality_descriptive</td>
							<td>careful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>normal</td>
							<td>quality_descriptive</td>
							<td>stupid</td>
							<td>quality_descriptive</td>
							<td>chilly</td>
							<td>negative-feeling_descriptive</td>
						</tr>
						<tr>
							<td>same</td>
							<td>indefinite</td>
							<td>long</td>
							<td>quality_descriptive</td>
							<td>rose taupe</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>striking</td>
							<td>quality_descriptive</td>
							<td>slight</td>
							<td>quality_descriptive</td>
							<td>tangerine yellow</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>future</td>
							<td>time_descriptive</td>
							<td>any</td>
							<td>indefinite</td>
							<td>liberal</td>
							<td>tone_descriptive</td>
						</tr>
						<tr>
							<td>maroon</td>
							<td>color_descriptive</td>
							<td>cuddly</td>
							<td>touch_descriptive</td>
							<td>electric green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>oval</td>
							<td>shape_descriptive</td>
							<td>light</td>
							<td>tone_descriptive</td>
							<td>underweight</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>azure</td>
							<td>color_descriptive</td>
							<td>magnolia</td>
							<td>color_descriptive</td>
							<td>atomic tagerine</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>mysterious</td>
							<td>negative-tone_descriptive</td>
							<td>grave</td>
							<td>quality_descriptive</td>
							<td>eager</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>dark blue</td>
							<td>color_descriptive</td>
							<td>fleet</td>
							<td>quality_descriptive</td>
							<td>zaffre-blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>tasty</td>
							<td>quality_descriptive</td>
							<td>selective yellow</td>
							<td>color_descriptive</td>
							<td>reclusive</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>fuschia pink</td>
							<td>color_descriptive</td>
							<td>peach-orange</td>
							<td>color_descriptive</td>
							<td>platinum</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>discerning</td>
							<td>quality_descriptive</td>
							<td>asparagus</td>
							<td>color_descriptive</td>
							<td>royal blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>straight</td>
							<td>shape_descriptive</td>
							<td>teeny-tiny</td>
							<td>size_descriptive</td>
							<td>unbalanced</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>afrikaans</td>
							<td>origin_descriptive</td>
							<td>angelic</td>
							<td>quality_descriptive</td>
							<td>icy</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>ruby</td>
							<td>color_descriptive</td>
							<td>evasive</td>
							<td>quality_descriptive</td>
							<td>understanding</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>courageous</td>
							<td>quality_descriptive</td>
							<td>irascible</td>
							<td>quality_descriptive</td>
							<td>attractive</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>beige</td>
							<td>color_descriptive</td>
							<td>dowdy</td>
							<td>quality_descriptive</td>
							<td>sapphire</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>lawn green</td>
							<td>color_descriptive</td>
							<td>trim</td>
							<td>size_descriptive</td>
							<td>all</td>
							<td>indefinite</td>
						</tr>
						<tr>
							<td>celadon</td>
							<td>color_descriptive</td>
							<td>speed</td>
							<td>quality_descriptive</td>
							<td>funny</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>pale-ect</td>
							<td>color_descriptive</td>
							<td>capable</td>
							<td>quality_descriptive</td>
							<td>cherry blossom</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>well-respected</td>
							<td>quality_descriptive</td>
							<td>khaki</td>
							<td>color_descriptive</td>
							<td>spherical</td>
							<td>shape_descriptive</td>
						</tr>
						<tr>
							<td>unsure</td>
							<td>quality_descriptive</td>
							<td>obliging</td>
							<td>quality_descriptive</td>
							<td>emotional</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cooperative</td>
							<td>quality_descriptive</td>
							<td>alizarin</td>
							<td>color_descriptive</td>
							<td>old lace</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>peevish</td>
							<td>quality_descriptive</td>
							<td>malachite</td>
							<td>color_descriptive</td>
							<td>petite</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>hateful</td>
							<td>quality_descriptive</td>
							<td>fabulous</td>
							<td>quality_descriptive</td>
							<td>terra cotta</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>hurtful</td>
							<td>negative-tone_descriptive</td>
							<td>no</td>
							<td>indefinite</td>
							<td>slimy</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>robin egg blue</td>
							<td>color_descriptive</td>
							<td>french</td>
							<td>origin_descriptive</td>
							<td>sympathetic</td>
							<td>positive-tone_descriptive</td>
						</tr>
						<tr>
							<td>ardent</td>
							<td>quality_descriptive</td>
							<td>relieved</td>
							<td>positive-feeling_descriptive</td>
							<td>emerald</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>pleasant</td>
							<td>quality_descriptive</td>
							<td>ok</td>
							<td>positive-feeling_descriptive</td>
							<td>fern green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>frosty</td>
							<td>touch_descriptive</td>
							<td>scandinavian</td>
							<td>origin_descriptive</td>
							<td>frustration</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>wonderful</td>
							<td>quality_descriptive</td>
							<td>carmine</td>
							<td>color_descriptive</td>
							<td>cowardly</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>czech</td>
							<td>origin_descriptive</td>
							<td>medium-ect</td>
							<td>color_descriptive</td>
							<td>loving</td>
							<td>positive-emotion_descriptive</td>
						</tr>
						<tr>
							<td>tactless</td>
							<td>quality_descriptive</td>
							<td>monthly</td>
							<td>time_descriptive</td>
							<td>powerful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>etruscan</td>
							<td>origin_descriptive</td>
							<td>melon</td>
							<td>color_descriptive</td>
							<td>considerate</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>russian</td>
							<td>origin_descriptive</td>
							<td>thinking</td>
							<td>quality_descriptive</td>
							<td>smalt</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>confident</td>
							<td>quality_descriptive</td>
							<td>experienced</td>
							<td>quality_descriptive</td>
							<td>vegas gold</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>lavender grey</td>
							<td>color_descriptive</td>
							<td>small</td>
							<td>size_descriptive</td>
							<td>listless</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>sepia</td>
							<td>color_descriptive</td>
							<td>numb</td>
							<td>quality_descriptive</td>
							<td>crafty</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>self-assured</td>
							<td>quality_descriptive</td>
							<td>scraggy</td>
							<td>size_descriptive</td>
							<td>indolent</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>irritated</td>
							<td>negative-tone_descriptive</td>
							<td>meddlesome</td>
							<td>quality_descriptive</td>
							<td>harequin</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>inspiring</td>
							<td>quality_descriptive</td>
							<td>respected</td>
							<td>quality_descriptive</td>
							<td>hawaiian</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>portuguese</td>
							<td>origin_descriptive</td>
							<td>unthinking</td>
							<td>quality_descriptive</td>
							<td>teensy</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>mauve taupe</td>
							<td>color_descriptive</td>
							<td>indiscreet</td>
							<td>quality_descriptive</td>
							<td>stern</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>crabby</td>
							<td>quality_descriptive</td>
							<td>mammoth</td>
							<td>size_descriptive</td>
							<td>idiotic</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>tamil</td>
							<td>origin_descriptive</td>
							<td>irritable</td>
							<td>quality_descriptive</td>
							<td>aggravated</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>idle</td>
							<td>quality_descriptive</td>
							<td>hollywood cerise</td>
							<td>color_descriptive</td>
							<td>razzmatazz</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>breezy</td>
							<td>touch_descriptive</td>
							<td>inconsistent</td>
							<td>quality_descriptive</td>
							<td>below average</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>illogical</td>
							<td>quality_descriptive</td>
							<td>bashful</td>
							<td>negative-feeling_descriptive</td>
							<td>old rose</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>defeated</td>
							<td>negative-feeling_descriptive</td>
							<td>auburn</td>
							<td>color_descriptive</td>
							<td>pocket-size</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>taupe</td>
							<td>color_descriptive</td>
							<td>inventive</td>
							<td>quality_descriptive</td>
							<td>egotistical</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>subtle</td>
							<td>quality_descriptive</td>
							<td>chinese</td>
							<td>origin_descriptive</td>
							<td>quick-tempered</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>versatile</td>
							<td>quality_descriptive</td>
							<td>burnt umber</td>
							<td>color_descriptive</td>
							<td>revered</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>life-size</td>
							<td>size_descriptive</td>
							<td>imperturbable</td>
							<td>quality_descriptive</td>
							<td>falu red</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>cornflower blue</td>
							<td>color_descriptive</td>
							<td>flax</td>
							<td>color_descriptive</td>
							<td>musical</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>long-winded</td>
							<td>quality_descriptive</td>
							<td>flat</td>
							<td>shape_descriptive</td>
							<td>disgruntled</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>paternalistic</td>
							<td>quality_descriptive</td>
							<td>heavy</td>
							<td>size_descriptive</td>
							<td>professional</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>searing</td>
							<td>touch_descriptive</td>
							<td>plucky</td>
							<td>quality_descriptive</td>
							<td>phlox- purple</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>stout</td>
							<td>size_descriptive</td>
							<td>miserable</td>
							<td>quality_descriptive</td>
							<td>deep-ect</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>thoughtful</td>
							<td>quality_descriptive</td>
							<td>thin</td>
							<td>size_descriptive</td>
							<td>arabic</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>indonesian</td>
							<td>origin_descriptive</td>
							<td>well-behaved</td>
							<td>quality_descriptive</td>
							<td>mauve</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>copper</td>
							<td>color_descriptive</td>
							<td>futuristic</td>
							<td>time_descriptive</td>
							<td>earnest</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>goldenrod</td>
							<td>color_descriptive</td>
							<td>aquamarine</td>
							<td>color_descriptive</td>
							<td>awful</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>snazzy</td>
							<td>quality_descriptive</td>
							<td>puce</td>
							<td>color_descriptive</td>
							<td>able</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>shocking pink</td>
							<td>color_descriptive</td>
							<td>supportive</td>
							<td>positive-emotion_descriptive</td>
							<td>carolina blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>various</td>
							<td>indefinite</td>
							<td>groggy</td>
							<td>quality_descriptive</td>
							<td>mountbatten pink</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>ochre</td>
							<td>color_descriptive</td>
							<td>not supportive</td>
							<td>negative-tone_descriptive</td>
							<td>deft</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>well-rounded</td>
							<td>quality_descriptive</td>
							<td>jade</td>
							<td>color_descriptive</td>
							<td>silver</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>old-fashioned</td>
							<td>quality_descriptive</td>
							<td>diligent</td>
							<td>quality_descriptive</td>
							<td>psychedelic purple</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>circumspect</td>
							<td>quality_descriptive</td>
							<td>rapid</td>
							<td>quality_descriptive</td>
							<td>mahogany</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>intent</td>
							<td>positive-tone_descriptive</td>
							<td>soulless</td>
							<td>quality_descriptive</td>
							<td>skeletal</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>hurt</td>
							<td>negative-emotion_descriptive</td>
							<td>blue</td>
							<td>color_descriptive</td>
							<td>violet</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>local</td>
							<td>quality_descriptive</td>
							<td>smart</td>
							<td>quality_descriptive</td>
							<td>intelligent</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>adventurous</td>
							<td>quality_descriptive</td>
							<td>very well</td>
							<td>positive-feeling_descriptive</td>
							<td>lavander</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>thrilled</td>
							<td>positive-emotion_descriptive</td>
							<td>conscientious</td>
							<td>quality_descriptive</td>
							<td>baby blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>pride</td>
							<td>positive-emotion_descriptive</td>
							<td>infinitesimal</td>
							<td>size_descriptive</td>
							<td>lanky</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>touchy</td>
							<td>quality_descriptive</td>
							<td>illimitable</td>
							<td>size_descriptive</td>
							<td>cordial</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cool-headed</td>
							<td>quality_descriptive</td>
							<td>serious</td>
							<td>quality_descriptive</td>
							<td>portly</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>outraged</td>
							<td>negative-tone_descriptive</td>
							<td>composed</td>
							<td>quality_descriptive</td>
							<td>cynical</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>chunky</td>
							<td>size_descriptive</td>
							<td>content</td>
							<td>quality_descriptive</td>
							<td>misty rose</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>hot</td>
							<td>negative-feeling_descriptive</td>
							<td>brilliant</td>
							<td>quality_descriptive</td>
							<td>minuscule</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>elastic</td>
							<td>touch_descriptive</td>
							<td>sumatran</td>
							<td>origin_descriptive</td>
							<td>guarded</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>shrewd</td>
							<td>quality_descriptive</td>
							<td>hypercritical</td>
							<td>quality_descriptive</td>
							<td>zealous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>frank</td>
							<td>quality_descriptive</td>
							<td>scanty</td>
							<td>size_descriptive</td>
							<td>golden yellow</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>elfin</td>
							<td>size_descriptive</td>
							<td>critical</td>
							<td>quality_descriptive</td>
							<td>chestnut</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>tagalog</td>
							<td>origin_descriptive</td>
							<td>peach-yellow</td>
							<td>color_descriptive</td>
							<td>punctual</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>copper rose</td>
							<td>color_descriptive</td>
							<td>magic mint</td>
							<td>color_descriptive</td>
							<td>happy</td>
							<td>positive-emotion_descriptive</td>
						</tr>
						<tr>
							<td>civil</td>
							<td>quality_descriptive</td>
							<td>hard</td>
							<td>touch_descriptive</td>
							<td>puny</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>easy-going</td>
							<td>quality_descriptive</td>
							<td>direct</td>
							<td>quality_descriptive</td>
							<td>dark coral</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>plump</td>
							<td>size_descriptive</td>
							<td>brillian rose</td>
							<td>color_descriptive</td>
							<td>inexperienced</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>slick</td>
							<td>touch_descriptive</td>
							<td>federal -blue</td>
							<td>color_descriptive</td>
							<td>careless</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>lucky</td>
							<td>positive-feeling_descriptive</td>
							<td>norwegian</td>
							<td>origin_descriptive</td>
							<td>keen</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>impressive</td>
							<td>quality_descriptive</td>
							<td>pale blue</td>
							<td>color_descriptive</td>
							<td>fear</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>light green</td>
							<td>color_descriptive</td>
							<td>squat</td>
							<td>size_descriptive</td>
							<td>bright green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>camouflage green</td>
							<td>color_descriptive</td>
							<td>office green</td>
							<td>color_descriptive</td>
							<td>round</td>
							<td>shape_descriptive</td>
						</tr>
						<tr>
							<td>irish</td>
							<td>origin_descriptive</td>
							<td>loose</td>
							<td>touch_descriptive</td>
							<td>conservative</td>
							<td>tone_descriptive</td>
						</tr>
						<tr>
							<td>absent-minded</td>
							<td>quality_descriptive</td>
							<td>fussy</td>
							<td>quality_descriptive</td>
							<td>boisterous</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>leery</td>
							<td>quality_descriptive</td>
							<td>white</td>
							<td>color_descriptive</td>
							<td>japanese</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>venal</td>
							<td>quality_descriptive</td>
							<td>iridescent blue</td>
							<td>color_descriptive</td>
							<td>willing</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>cardinal red</td>
							<td>color_descriptive</td>
							<td>helpful</td>
							<td>quality_descriptive</td>
							<td>charming</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>unpleasant</td>
							<td>quality_descriptive</td>
							<td>passive</td>
							<td>quality_descriptive</td>
							<td>irregular</td>
							<td>time_descriptive</td>
						</tr>
						<tr>
							<td>clumsy</td>
							<td>quality_descriptive</td>
							<td>natural</td>
							<td>quality_descriptive</td>
							<td>devoted</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>festive</td>
							<td>positive-feeling_descriptive</td>
							<td>cantankerous</td>
							<td>quality_descriptive</td>
							<td>slovak</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>grumpy</td>
							<td>negative-emotion_descriptive</td>
							<td>well</td>
							<td>positive-feeling_descriptive</td>
							<td>optimistic</td>
							<td>positive-feeling_descriptive</td>
						</tr>
						<tr>
							<td>bright-ect</td>
							<td>color_descriptive</td>
							<td>realistic</td>
							<td>quality_descriptive</td>
							<td>dumb</td>
							<td>negative-feeling_descriptive</td>
						</tr>
						<tr>
							<td>hunter -yellow</td>
							<td>color_descriptive</td>
							<td>tolerant</td>
							<td>quality_descriptive</td>
							<td>apathetic</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>persimmon</td>
							<td>color_descriptive</td>
							<td>ivory</td>
							<td>color_descriptive</td>
							<td>plain</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>rust</td>
							<td>color_descriptive</td>
							<td>incisive</td>
							<td>quality_descriptive</td>
							<td>overwhelmed</td>
							<td>negative-emotion_descriptive</td>
						</tr>
						<tr>
							<td>dry</td>
							<td>touch_descriptive</td>
							<td>safety orange</td>
							<td>color_descriptive</td>
							<td>dull</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>tepid</td>
							<td>touch_descriptive</td>
							<td>sincere</td>
							<td>quality_descriptive</td>
							<td>javanese</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>lame</td>
							<td>quality_descriptive</td>
							<td>glutinous</td>
							<td>quality_descriptive</td>
							<td>spring green</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>joyful</td>
							<td>positive-emotion_descriptive</td>
							<td>yiddish</td>
							<td>origin_descriptive</td>
							<td>gaunt</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>mature</td>
							<td>quality_descriptive</td>
							<td>rose madder</td>
							<td>color_descriptive</td>
							<td>microscopic</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>saucy</td>
							<td>quality_descriptive</td>
							<td>certain</td>
							<td>indefinite</td>
							<td>denmark</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>deep lilac</td>
							<td>color_descriptive</td>
							<td>tight</td>
							<td>touch_descriptive</td>
							<td>secular</td>
							<td>tone_descriptive</td>
						</tr>
						<tr>
							<td>brief</td>
							<td>quality_descriptive</td>
							<td>impetuous</td>
							<td>quality_descriptive</td>
							<td>international klein boue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>urdu</td>
							<td>origin_descriptive</td>
							<td>selfish</td>
							<td>quality_descriptive</td>
							<td>shaggy</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>sober</td>
							<td>quality_descriptive</td>
							<td>tea rose</td>
							<td>color_descriptive</td>
							<td>slovenly</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>tangerine</td>
							<td>color_descriptive</td>
							<td>hulking</td>
							<td>size_descriptive</td>
							<td>malay</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>angry</td>
							<td>negative-tone_descriptive</td>
							<td>energetic</td>
							<td>quality_descriptive</td>
							<td>childlike</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>sarcastic</td>
							<td>negative-tone_descriptive</td>
							<td>dank</td>
							<td>touch_descriptive</td>
							<td>filthy</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>open</td>
							<td>positive-feeling_descriptive</td>
							<td>slim</td>
							<td>size_descriptive</td>
							<td>ancient</td>
							<td>age_descriptive</td>
						</tr>
						<tr>
							<td>damp</td>
							<td>touch_descriptive</td>
							<td>agile</td>
							<td>quality_descriptive</td>
							<td>unstable</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>alert</td>
							<td>quality_descriptive</td>
							<td>guilty</td>
							<td>negative-tone_descriptive</td>
							<td>sturdy</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>imaginative</td>
							<td>quality_descriptive</td>
							<td>ambitious</td>
							<td>quality_descriptive</td>
							<td>plum</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>spring bud</td>
							<td>color_descriptive</td>
							<td>golden brown</td>
							<td>color_descriptive</td>
							<td>impatient</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>turquoise</td>
							<td>color_descriptive</td>
							<td>tired</td>
							<td>negative-feeling_descriptive</td>
							<td>xanthic</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>dreary</td>
							<td>quality_descriptive</td>
							<td>evil</td>
							<td>negative-feeling_descriptive</td>
							<td>tan</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>shame</td>
							<td>negative-emotion_descriptive</td>
							<td>cerulean blue</td>
							<td>color_descriptive</td>
							<td>persevering</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>eggplant</td>
							<td>color_descriptive</td>
							<td>school bus yellow</td>
							<td>color_descriptive</td>
							<td>kind</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>triangular</td>
							<td>shape_descriptive</td>
							<td>fiery</td>
							<td>quality_descriptive</td>
							<td>amaranth</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>chartreuse</td>
							<td>color_descriptive</td>
							<td>epic</td>
							<td>size_descriptive</td>
							<td>overweight</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>olivine</td>
							<td>color_descriptive</td>
							<td>hot-headed</td>
							<td>quality_descriptive</td>
							<td>gigantic</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>boundless</td>
							<td>size_descriptive</td>
							<td>responsible</td>
							<td>quality_descriptive</td>
							<td>rich carmine</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>jealousy</td>
							<td>negative-emotion_descriptive</td>
							<td>sulky</td>
							<td>quality_descriptive</td>
							<td>ostentatious</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>lemon chiffon</td>
							<td>color_descriptive</td>
							<td>magenta</td>
							<td>color_descriptive</td>
							<td>oxford blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>paternal</td>
							<td>quality_descriptive</td>
							<td>blood red</td>
							<td>color_descriptive</td>
							<td>old</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>methodical</td>
							<td>quality_descriptive</td>
							<td>sundanese</td>
							<td>origin_descriptive</td>
							<td>little</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>lavender blush</td>
							<td>color_descriptive</td>
							<td>trifling</td>
							<td>size_descriptive</td>
							<td>dreadful</td>
							<td>negative-feeling_descriptive</td>
						</tr>
						<tr>
							<td>silly</td>
							<td>negative-feeling_descriptive</td>
							<td>grand</td>
							<td>size_descriptive</td>
							<td>positive</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>impressionable</td>
							<td>quality_descriptive</td>
							<td>bright pink</td>
							<td>color_descriptive</td>
							<td>nervous</td>
							<td>negative-feeling_descriptive</td>
						</tr>
						<tr>
							<td>immeasurable</td>
							<td>size_descriptive</td>
							<td>carnation pink</td>
							<td>color_descriptive</td>
							<td>persnickety</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>rough</td>
							<td>touch_descriptive</td>
							<td>sangria</td>
							<td>color_descriptive</td>
							<td>disbelieving</td>
							<td>negative-tone_descriptive</td>
						</tr>
						<tr>
							<td>unmotivated</td>
							<td>quality_descriptive</td>
							<td>silky</td>
							<td>touch_descriptive</td>
							<td>excitable</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>forest green</td>
							<td>color_descriptive</td>
							<td>somber</td>
							<td>quality_descriptive</td>
							<td>popular</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>ice white</td>
							<td>color_descriptive</td>
							<td>talented</td>
							<td>quality_descriptive</td>
							<td>cylindrical</td>
							<td>shape_descriptive</td>
						</tr>
						<tr>
							<td>efficient</td>
							<td>quality_descriptive</td>
							<td>other</td>
							<td>indefinite</td>
							<td>warm</td>
							<td>touch_descriptive</td>
						</tr>
						<tr>
							<td>bony</td>
							<td>size_descriptive</td>
							<td>analytical</td>
							<td>quality_descriptive</td>
							<td>bondi blue</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>pretty</td>
							<td>quality_descriptive</td>
							<td>electric-ect</td>
							<td>color_descriptive</td>
							<td>slate grey</td>
							<td>color_descriptive</td>
						</tr>
						<tr>
							<td>flame</td>
							<td>color_descriptive</td>
							<td>miniature</td>
							<td>size_descriptive</td>
							<td>minute</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>inactive</td>
							<td>quality_descriptive</td>
							<td>wary</td>
							<td>quality_descriptive</td>
							<td>bulky</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>above average</td>
							<td>quality_descriptive</td>
							<td>yellow-red</td>
							<td>color_descriptive</td>
							<td>mean</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>immature</td>
							<td>quality_descriptive</td>
							<td>melted</td>
							<td>touch_descriptive</td>
							<td>skinny</td>
							<td>size_descriptive</td>
						</tr>
						<tr>
							<td>wheat</td>
							<td>color_descriptive</td>
							<td>sharp</td>
							<td>touch_descriptive</td>
							<td>love-lorn</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>steep</td>
							<td>shape_descriptive</td>
							<td>light-ect</td>
							<td>color_descriptive</td>
							<td>disruptive</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>bronze</td>
							<td>color_descriptive</td>
							<td>stocky</td>
							<td>size_descriptive</td>
							<td>superficial</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>massive</td>
							<td>size_descriptive</td>
							<td>decisive</td>
							<td>quality_descriptive</td>
							<td>latin</td>
							<td>origin_descriptive</td>
						</tr>
						<tr>
							<td>flaky</td>
							<td>quality_descriptive</td>
							<td>danish</td>
							<td>origin_descriptive</td>
							<td>negative</td>
							<td>quality_descriptive</td>
						</tr>
						<tr>
							<td>pastel green</td>
							<td>color_descriptive</td>
					</table>
				</div>
			</div>
		</div>
	</div>



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
