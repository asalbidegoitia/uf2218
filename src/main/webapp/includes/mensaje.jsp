<c:if test="${mensaje != null}">
	<div class="alert alert-${mensaje.tipo} alert-dismissible fade show"
		role="alert">
		<p>${mensaje.texto}</p>
		<button type="button" class="close" data-dismiss="alert"
			aria-label="Close">
			<span aria-hidden="true">&times;</span>
		</button>
	</div>
	<% //despues de mostrar poner el mensaje a null para no mostrarlo de nuevo %>
	${mensaje = null}
</c:if>
