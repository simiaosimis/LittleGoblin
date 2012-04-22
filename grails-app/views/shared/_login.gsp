	<div id='login'>
		<div class='inner'>
			<g:if test='${flash.message}'>
			<div class='login_message'>${flash.message}</div>
			</g:if>
			<div class='fheader'><g:message code="login.please"/></div>
			<form action='${postUrl}' method='POST' id='loginForm' class='cssform'>
				<p>
					<label for='j_username'><g:message code="login.username"/></label>
					<input type='text' class='text_' name='j_username' id='j_username' value='${request.remoteUser ?: 'anon'}' />
				</p>
				<p>
					<label for='j_password'><g:message code="login.password"/></label>
					<input type='password' class='text_' name='j_password' id='j_password' value='anon' />
				</p>
				<p>
					<label for='remember_me'><g:message code="login.remember_me"/></label>
					<input type='checkbox' class='chk' name='_spring_security_remember_me' id='remember_me'
					<g:if test='${hasCookie}'>checked='checked'</g:if> />
				</p>
				<p>
					<input type='submit' value='Login' />
				</p>
			</form>
		</div>
	</div>
<script type='text/javascript'>
<!--
(function(){
	document.forms['loginForm'].elements['j_username'].focus();
})();
// -->
</script>

	<div class="quick_login" style="text-align:center;">
		<form action='${postUrl}' method='POST' id='loginFormAnon' class='cssform'>
			<input type="hidden" name="j_username" value="anon">
			<input type="hidden" name="j_password" value="anon">
			<input type='submit' value='login as test user' />
		</form>
		<form action='${postUrl}' method='POST' id='loginFormAdmin' class='cssform'>
			<input type="hidden" name="j_username" value="admin">
			<input type="hidden" name="j_password" value="admin">
			<input type='submit' value='login as test admin' />
		</form>

	</div>