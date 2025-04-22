/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CapaNegocio;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

class ConnectionDatabase {

    // Datos de conexión
    private static final String URL = "jdbc:postgresql://www.tecnoweb.org.bo/db_agenda";
    private static final String USUARIO = "agenda";
    private static final String CONTRASEÑA = "agendaagenda";
    private Connection conexion;

    // Constructor
    public ConnectionDatabase() {
        this.conexion = null;
    }

    // Método para conectar a la base de datos
    public void conectar() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para desconectar de la base de datos
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca todas las coincidencias del patrón en campos de texto de la tabla
     * persona
     *
     * @return String formateado con los resultados
     */
    /**
     * Busca todas las coincidencias del patrón en campos de texto de la tabla
     * persona
     *
     * @return String formateado con los resultados
     */
    public String buscarPersonasPorNombre(String nombre) {
        StringBuilder resultado = new StringBuilder();

        if (conexion == null) {
            return "Conexión no establecida.";
        }

        // NO Sensible a mayúsculas y minúsculas con "LIKE"
        String consulta = "SELECT * FROM persona WHERE per_nom LIKE '%" + nombre + "%'";
        try {
            Statement declaracion = conexion.createStatement();
            ResultSet resultado_query = declaracion.executeQuery(consulta);
            ResultSetMetaData metaDatos = resultado_query.getMetaData();
            int numColumnas = metaDatos.getColumnCount();

            // Encabezados
            for (int i = 1; i <= numColumnas; i++) {
                resultado.append(metaDatos.getColumnName(i)).append("\t");
            }
            resultado.append("\n");

            // Separador
            for (int i = 1; i <= numColumnas; i++) {
                resultado.append("---------------").append("\t");
            }
            resultado.append("\n");

            // Datos
            int contadorFilas = 0;
            while (resultado_query.next()) {
                contadorFilas++;
                for (int i = 1; i <= numColumnas; i++) {
                    String valor = resultado_query.getString(i);
                    resultado.append(valor != null ? valor : "NULL").append("\t");
                }
                resultado.append("\n");
            }

            // Resumen
            resultado.append("\nTotal de registros encontrados: ").append(contadorFilas);

        } catch (SQLException e) {
            resultado.append("Error en la consulta: ").append(e.getMessage());
            e.printStackTrace();
        }

        return resultado.toString();
    }
}
