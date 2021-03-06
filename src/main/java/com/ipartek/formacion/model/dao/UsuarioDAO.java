package com.ipartek.formacion.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.ipartek.formacion.model.ConnectionManager;
import com.ipartek.formacion.model.pojo.Rol;
import com.ipartek.formacion.model.pojo.Usuario;

public class UsuarioDAO {
	private static UsuarioDAO INSTANCE = null;

	private static final String SQL_EXISTE = "SELECT u.id, u.nombre, r.id AS 'id_rol', r.nombre AS 'nombre_rol', contrasenya, fecha_creacion, fecha_eliminacion FROM usuario AS u, rol AS r WHERE u.id_rol = r.id AND u.nombre = ? AND u.contrasenya = ?;";
	private static final String SQL_GET_ALL = "SELECT u.id, u.nombre, r.id AS 'id_rol', r.nombre AS 'nombre_rol', contrasenya, fecha_creacion, fecha_eliminacion FROM usuario AS u, rol AS r WHERE u.id_rol = r.id ORDER BY u.id ASC LIMIT 500;";
	private static final String SQL_GET_ALL_VISIBLE = "SELECT u.id, u.nombre, r.id AS 'id_rol', r.nombre AS 'nombre_rol', contrasenya, fecha_creacion, fecha_eliminacion FROM usuario AS u, rol AS r WHERE u.id_rol = r.id AND fecha_eliminacion IS NULL ORDER BY u.id ASC LIMIT 500;";
	private static final String SQL_GET_ALL_NOT_VISIBLE = "SELECT u.id, u.nombre, r.id AS 'id_rol', r.nombre AS 'nombre_rol', contrasenya, fecha_creacion, fecha_eliminacion FROM usuario AS u, rol AS r WHERE u.id_rol = r.id AND fecha_eliminacion IS NOT NULL ORDER BY u.id ASC LIMIT 500;";
	private static final String SQL_GET_BY_ID = "SELECT u.id, u.nombre, r.id AS 'id_rol', r.nombre AS 'nombre_rol', contrasenya, fecha_creacion, fecha_eliminacion FROM usuario AS u, rol AS r WHERE u.id_rol = r.id AND u.id = ?;";
	private static final String SQL_UPDATE = "UPDATE usuario SET nombre = ?, contrasenya = ? WHERE  id = ?;";
	// private static final String SQL_DELETE = "DELETE FROM usuario WHERE id = ?;";
	private static final String SQL_DELETE_LOGICO = "UPDATE usuario SET fecha_eliminacion = CURRENT_TIMESTAMP WHERE id = ? ";
	private static final String SQL_INSERT = "INSERT INTO usuario (nombre, contrasenya) VALUES (?,?);";
	private static final String SQL_GET_BY_NAME = "SELECT u.id, u.nombre, r.id AS 'id_rol', r.nombre AS 'nombre_rol', contrasenya, fecha_creacion, fecha_eliminacion FROM usuario AS u, rol AS r WHERE u.id_rol = r.id AND u.nombre LIKE ? ORDER BY u.id ASC LIMIT 500;";
	private static String sql = "";

	private UsuarioDAO() {
		super();
	}

	// synchronized sirve para que varias ejecuciones no puedan entrar al mismo
	// tiempo en un metodo
	public static synchronized UsuarioDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UsuarioDAO();
		}
		return INSTANCE;
	}

	/**
	 * Comprueba si existe el usuario en la base de datos, lo busca por su nombre y
	 * contrasenya
	 * 
	 * @param nombre
	 * @param contrasenya
	 * @return Usuario con datos si existe, null en caso de no existir
	 * @throws SQLException
	 */
	public Usuario existe(String nombre, String contrasenya) {
		Usuario usuario = null;
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_EXISTE);) {

			// sustituir ? por parametros
			pst.setString(1, nombre);
			pst.setString(2, contrasenya);

			// ejecutar sentencia SQL y obtener Resultado
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					usuario = new Usuario();
					usuario.setId(rs.getInt("id"));
					usuario.setNombre(rs.getString("nombre"));
					usuario.setContrasenya(rs.getString("contrasenya"));
					usuario.setFechaCreacion(rs.getDate("fecha_creacion"));
					usuario.setFechaEliminacion(rs.getDate("fecha_eliminacion"));

					Rol rol = new Rol();
					rol.setId(rs.getInt("id_rol"));
					rol.setNombre(rs.getString("nombre_rol"));
					usuario.setRol(rol);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return usuario;
	}

	public ArrayList<Usuario> getAll() {
		ArrayList<Usuario> lista = new ArrayList<Usuario>();
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_GET_ALL);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				lista.add(mapper(rs));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return lista;
	}

	public ArrayList<Usuario> getAllVisible(Boolean isVisible) {

		ArrayList<Usuario> lista = new ArrayList<Usuario>();

		if (isVisible) {
			sql = SQL_GET_ALL_VISIBLE;
		} else {
			sql = SQL_GET_ALL_NOT_VISIBLE;
		}
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(sql);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				lista.add(mapper(rs));
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return lista;
	}

	public Usuario getById(int id) {
		Usuario user = new Usuario();
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_GET_BY_ID)) {
			// sustituyo la 1º ? por la variable id
			pst.setInt(1, id);

			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					user = mapper(rs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean modificar(Usuario pojo) throws Exception {
		boolean resultado = false;
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_UPDATE)) {

			pst.setString(1, pojo.getNombre());
			pst.setString(2, pojo.getContrasenya());
			pst.setInt(3, pojo.getId());

			int affectedRows = pst.executeUpdate();
			if (affectedRows == 1) {
				resultado = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultado;
	}

	public Usuario crear(Usuario pojo) throws Exception {

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			pst.setString(1, pojo.getNombre());
			pst.setString(2, pojo.getContrasenya());

			int affectedRows = pst.executeUpdate();
			if (affectedRows == 1) {
				// conseguimos el ID que acabamos de crear
				ResultSet rs = pst.getGeneratedKeys();
				if (rs.next()) {
					pojo.setId(rs.getInt(1));
				}
			}
		}
		return pojo;
	}

	public boolean eliminar(int id) {
		boolean resultado = false;
		// String sql = "DELETE FROM usuario WHERE id = ?;";

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_DELETE_LOGICO);) {

			pst.setInt(1, id);

			int affetedRows = pst.executeUpdate();
			if (affetedRows == 1) {
				resultado = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultado;
	}

	public ArrayList<Usuario> getAllByName(String buscar) {
		ArrayList<Usuario> lista = new ArrayList<Usuario>();
		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SQL_GET_BY_NAME)) {

			pst.setString(1, '%' + buscar + '%'); // busca todos, los que contengan esa letra/palabra
			// pst.setString(1,buscar + '%'); //busca los que empiecen por esa letra/palabra

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					lista.add(mapper(rs));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	public Usuario mapper(ResultSet rs) throws SQLException {
		Usuario u = new Usuario();
		u.setId(rs.getInt("id"));
		u.setNombre(rs.getString("nombre"));
		u.setContrasenya(rs.getString("contrasenya"));
		u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
		u.setFechaEliminacion(rs.getTimestamp("fecha_eliminacion"));

		Rol rol = new Rol();
		rol.setId(rs.getInt("id_rol"));
		rol.setNombre(rs.getString("nombre_rol"));
		u.setRol(rol);

		return u;
	}
}
