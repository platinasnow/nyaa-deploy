<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="header.jspf" %>
	<div class="contents-wrap container-fluid">
		<table class="table table-striped">
			${contents}
		</table>
		<div class="pagination-wrap">
		<ul class="pagination pagination-sm">
			<li><a href='javascript:movePage("${pages.prev[0]}")'>&lt;</a></li>
			<c:forEach items="${pages.list }" var="page" >
				<c:if test="${page == pages.current[0]}"><li class='active'><a href='#'>${pages.current[0] }</a></li></c:if>
				<c:if test="${page != pages.current[0]}"><li><a href='javascript:movePage("${page}")'>${page}</a></li></c:if>
			</c:forEach>
			<li><a href='javascript:movePage("${pages.next[0]}")'>&gt;</a></li>
		</ul>
		</div>
	</div>
	<form class="urlForm" method="post"> 
		<input type="hidden" name="url" class="url" />
	</form>

<%@include file="footer.jspf" %>
<script type="text/javascript">

var movePage = function(idx){
	$('.offset').val(idx);
	goPage();
};
var urlForm = $('.urlForm')[0];

$(function(){
	$('.tlistname').children('a').each(function(){
		$(this).attr('data-url', $(this).attr('href')).removeAttr('href');
	});
	
	$('.tlistname').on('click', function(){
		var url = $(this).children('a').attr('data-url');
		$('.url').val(url);
		urlForm.action='v';
		urlForm.submit();
	});
});

</script>
</html>