<%@page import="com.ipartek.formacion.controller.CalculadoraController"%>
<%@include file="../includes/header.jsp"%>
<%@include file="../includes/navbar.jsp"%>

<%@include file="../includes/mensaje.jsp"%>

<h1>Calculadora</h1>
<form action="calculadora" method="post">
	<div class="form-group">
		<label for="inputNum1"
			class="col-sm-3 col-form-label col-form-label-lg">N�mero 1</label>
			 <input
			type="number" step="0.01" class="form-control" name="numero1" id="inputNum1"
			placeholder="Introduce un n�mero" value="${numero1}">
	</div>
	
	<div class="form-group">
		<label for="inputNum2"
			class="col-sm-3 col-form-label col-form-label-lg">N�mero 2</label> 
			<input
			type="number" step="0.01" class="form-control" name="numero2" id="inputNum2"
			placeholder="Introduce un n�mero" value="${numero2}">
	</div>
	<select name="op" class="form-control" value="${op}">
		<option value="">Selecciona una operaci�n</option>
 		<option value="<%= CalculadoraController.OP_SUMAR %>">Sumar</option>
 		<option value="<%= CalculadoraController.OP_RESTAR %>">Restar</option>
 		<option value="<%= CalculadoraController.OP_MULTIPLICAR %>">Multiplicar</option>
 		<option value="<%= CalculadoraController.OP_DIVIDIR %>">Dividir</option>
	</select>


	<button type="submit" class="btn btn-primary mt-2 mb-2 btn-block">Calcular</button>
</form>	
	<div class="resultado">
		<label for="resultado"
			class="col-sm-3 col-form-label col-form-label-lg">Resultado: ${resultado}
		</label>
	</div>

<%@include file="../includes/footer.jsp"%>