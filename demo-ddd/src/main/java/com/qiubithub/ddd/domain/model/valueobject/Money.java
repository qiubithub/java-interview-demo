package com.qiubithub.ddd.domain.model.valueobject;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * 金额值对象
 */
@Getter
public class Money implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal amount;
    private String currencyCode;

    protected Money() {
        // JPA需要无参构造函数
    }

    public Money(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            throw exception(ErrorCode.AMOUNT_CANNOT_BE_NULL);
        }
        if (StringUtils.isBlank(currencyCode)) {
            throw exception(ErrorCode.CURRENCY_CODE_CANNOT_BE_EMPTY);
        }
        
        // 验证货币代码有效性
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw exception(ErrorCode.INVALID_CURRENCY_CODE);
        }
        
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currencyCode = currencyCode;
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, currencyCode);
    }

    public static Money yuan(BigDecimal amount) {
        return new Money(amount, "CNY");
    }

    public static Money yuan(double amount) {
        return new Money(BigDecimal.valueOf(amount), "CNY");
    }

    public Money add(Money money) {
        validateSameCurrency(money);
        return new Money(this.amount.add(money.amount), this.currencyCode);
    }

    public Money subtract(Money money) {
        validateSameCurrency(money);
        return new Money(this.amount.subtract(money.amount), this.currencyCode);
    }

    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currencyCode);
    }

    public Money multiply(double multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currencyCode);
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isZero() {
        return BigDecimal.ZERO.compareTo(this.amount) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private void validateSameCurrency(Money money) {
        if (!this.currencyCode.equals(money.currencyCode)) {
            throw exception(ErrorCode.CURRENCY_MISMATCH);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currencyCode.equals(money.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currencyCode);
    }

    @Override
    public String toString() {
        return amount.toString() + " " + currencyCode;
    }

    private RuntimeException exception(ErrorCode errorCode) {
        return new IllegalArgumentException(errorCode.getMessage());
    }

    /**
     * 金额相关错误码
     */
    public enum ErrorCode {
        AMOUNT_CANNOT_BE_NULL("金额不能为空"),
        CURRENCY_CODE_CANNOT_BE_EMPTY("货币代码不能为空"),
        INVALID_CURRENCY_CODE("无效的货币代码"),
        CURRENCY_MISMATCH("货币类型不匹配");

        private final String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}