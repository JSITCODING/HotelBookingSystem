package model;
import java.util.Objects;
import java.io.Serializable;

public class quarto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numero;
    private String tipo;
    private double preco;

    public quarto(String numero, String tipo, double preco) {
        this.numero = numero;
        this.tipo = tipo;
        this.preco = preco;
    }

    public String getNumero() {
        return numero;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof quarto)) return false;
        quarto q = (quarto) o;
        return Objects.equals(numero, q.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return "Quarto{" +
                "numero='" + numero + '\'' +
                ", tipo='" + tipo + '\'' +
                ", preco=" + preco +
                '}';
    }
}