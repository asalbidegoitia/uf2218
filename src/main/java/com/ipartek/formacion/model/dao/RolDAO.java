package com.ipartek.formacion.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ipartek.formacion.model.ConnectionManager;
import com.ipartek.formacion.model.pojo.Rol;

public class RolDAO {
	private static RolDAO INSTANCE = null;

	private static final String SQL_GET_ALL = "SELECT id, nombre FROM rol ORDER BY id ASC LIMIT 500;";

	private RolDAO() {
		super();
	}

	public static RolDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new RolDAO();
		}
		return INSTANCE;
	}

	public ArrayList<Rol> getAll() {

		ArrayList<Rol> lista = new ArrayList<Rol>();

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

	public Rol mapper(ResultSet rs) throws SQLException {
		Rol rol = new Rol();
		rol.setId(rs.getInt("id"));
		rol.setNombre(rs.getString("nombre"));
		return rol;
	}

}
