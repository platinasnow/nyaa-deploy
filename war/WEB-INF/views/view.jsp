<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="header.jspf" %>
	<div class="contents-wrap container-fluid">
		<div class="panel panel-success">
			<div class="panel-heading">
			
			</div>
			<div class="panel-body view-contents">
				<table class="table information-table">
					${view.information }
				</table>
				<div class="down-wrap">
					${view.txtDownBtn }
					${view.torrentDownBtn }
				</div>
				<div class="description-wrap">
					${view.description }
				</div>
			</div>
		</div>
		<div class="panel panel-success">
			<div class="panel-heading">Files in torrent</div>
			<div class="panel-body ">
				<table class="table">
					${view.files }				
				</table>
			</div>
		</div>
	</div>
<%@include file="footer.jspf" %>
<script type="text/javascript">
$(function(){
	$('.information-table tr td:eq(9)').css('word-break', 'break-all');
});

</script>
</html>