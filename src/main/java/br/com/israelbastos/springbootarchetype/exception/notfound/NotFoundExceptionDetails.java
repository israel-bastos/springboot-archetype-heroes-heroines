package br.com.israelbastos.springbootarchetype.exception.notfound;

import br.com.israelbastos.springbootarchetype.exception.ExceptionDetails;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class NotFoundExceptionDetails extends ExceptionDetails { }
