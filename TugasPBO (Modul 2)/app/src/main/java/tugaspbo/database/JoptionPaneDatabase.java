package tugaspbo.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JoptionPaneDatabase {
    static Scanner sc = new Scanner(System.in);
    
    static class Jurusan{
        String kode;
        String nama;
        ArrayList <MataKuliah> matkulList = new ArrayList<>();

        public Jurusan(String kode, String nama){
            this.kode = kode;
            this.nama = nama;
        }
    }

    static class Mahasiswa{
        String nim;
        String nama;
        Jurusan jurusan;
        Map<MataKuliah, String> indexNilai = new HashMap<>();

        public Mahasiswa(String nim, String nama, Jurusan jurusan){
            this.nim = nim;
            this.nama = nama;
            this.jurusan = jurusan;
        }
    }

    static class MataKuliah{
        String kode;
        String nama;
        int sks;

        public MataKuliah(String kode, String nama, int sks){
            this.kode = kode;
            this.nama = nama;
            this.sks = sks;
        }
    }

    class aList{
        static ArrayList<Mahasiswa> mhsList = new ArrayList<>();
        static ArrayList<Jurusan> jurusanList = new ArrayList<>();

        // INSERT Jurusan ke Database
        public static void tambahJurusan() {
            boolean loop = true;
            do{
                String kode = JOptionPane.showInputDialog("Masukkan kode jurusan: ");
                String nama = JOptionPane.showInputDialog("Masukkan Nama jurusan: ");

                // Insert ke database
                if (insertJurusanToDatabase(kode, nama)) {
                    Jurusan jurusan = new Jurusan(kode, nama);
                    jurusanList.add(jurusan);
                    JOptionPane.showMessageDialog(null, "Jurusan berhasil ditambahkan ke database!");
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menambahkan jurusan ke database!");
                }

                String pilihan = JOptionPane.showInputDialog("Mau Memasukan lagi? (ya/tidak)");
                if (!pilihan.equalsIgnoreCase("ya")) {
                    loop = false;
                    Menu();
                }
            } while (loop == true);
        }

        // INSERT Mahasiswa ke Database
        public static void tambahMhs() {
            boolean loop = true;
            do{
                String nim = JOptionPane.showInputDialog("Masukkan NIM: ");
                String nama = JOptionPane.showInputDialog("Masukkan Nama: ");

                // Load jurusan dari database
                loadJurusanFromDatabase();
                
                String daftarJurusan = "pilih jurusan: \n";
                if (jurusanList.size() <= 0){
                    JOptionPane.showMessageDialog(null, "Jurusan masih belom ada, input mahasiswa dibatalkan");
                }else{
                    for (int i = 0; i < jurusanList.size(); i++){
                        daftarJurusan += (i+1) + ". " + jurusanList.get(i).nama + "\n";
                    }
                    int pilih = Integer.parseInt(JOptionPane.showInputDialog(daftarJurusan)) - 1;
                    Jurusan jurusan = jurusanList.get(pilih);

                    // Insert ke database
                    if (insertMahasiswaToDatabase(nim, nama, jurusan.kode)) {
                        Mahasiswa mhs = new Mahasiswa(nim, nama, jurusan);
                        mhsList.add(mhs);
                        JOptionPane.showMessageDialog(null, "Mahasiswa berhasil ditambahkan ke database!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal menambahkan mahasiswa ke database!");
                    }
                }

                String pilihan = JOptionPane.showInputDialog("Mau Memasukan lagi? (ya/tidak)");
                if (!pilihan.equalsIgnoreCase("ya")) {
                    loop = false;
                    Menu();
                }
            } while (loop == true);
        }

        // INSERT Mata Kuliah ke Database
        public static void tambahMatkul(){
            boolean loop = true;
            
            // Load jurusan dari database
            loadJurusanFromDatabase();
            
            if (jurusanList.isEmpty()){
                JOptionPane.showMessageDialog(null,"Belum adanya Jurusan, Tolong tambahkan jurusan terlebih dahulu");
                loop = false;
                Menu();
            }

            do{
                String daftarJurusan = "Pilih Jurusan: \n";
                for(int i = 0; i < jurusanList.size(); i++){
                    daftarJurusan += (i+1) + ". " + jurusanList.get(i).nama + "\n";
                }

                int pilih = Integer.parseInt(JOptionPane.showInputDialog(daftarJurusan)) - 1;
                Jurusan jurusan = jurusanList.get(pilih);

                String kode = JOptionPane.showInputDialog("Masukan Kode Mata Kuliah");
                String nama = JOptionPane.showInputDialog("Masukan nama Mata Kuliah");
                int sks = Integer.parseInt(JOptionPane.showInputDialog("Berapa sks kah mata kuliah ini?"));

                // Insert ke database
                if (insertMataKuliahToDatabase(kode, nama, sks, jurusan.kode)) {
                    MataKuliah mk = new MataKuliah(kode, nama, sks);
                    jurusan.matkulList.add(mk);
                    JOptionPane.showMessageDialog(null, "Mata kuliah berhasil ditambahkan ke database!");
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menambahkan mata kuliah ke database!");
                }

                String pilihan = JOptionPane.showInputDialog("Apakah ada mata kuliah lainnya yang ingin dimasukan??");
                if (!pilihan.equalsIgnoreCase("ya")){
                    loop = false;
                    Menu();
                }
            }while(loop == true);
        }

        // INSERT Nilai ke Database
        public static void tambahNilai() {
            // Load data dari database
            loadMahasiswaFromDatabase();
            
            if (aList.mhsList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Belum ada mahasiswa.");
                Menu();
                return;
            }
            String daftarMhs = "Pilih Mahasiswa:\n";
            for (int i = 0; i < aList.mhsList.size(); i++) {
                daftarMhs += (i + 1) + ". " + aList.mhsList.get(i).nama + " (" + aList.mhsList.get(i).nim + ")\n";
            }
            int pilihMhs = Integer.parseInt(JOptionPane.showInputDialog(daftarMhs)) - 1;
            Mahasiswa mhs = aList.mhsList.get(pilihMhs);

            Jurusan jurusan = mhs.jurusan;
            if (jurusan.matkulList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Jurusan belum memiliki mata kuliah.");
                Menu();
                return;
            }

            String daftarMatkul = "Pilih Mata Kuliah:\n";
            for (int i = 0; i < jurusan.matkulList.size(); i++) {
                daftarMatkul += (i + 1) + ". " + jurusan.matkulList.get(i).nama + "\n";
            }
            int pilihMk = Integer.parseInt(JOptionPane.showInputDialog(daftarMatkul)) - 1;
            MataKuliah mk = jurusan.matkulList.get(pilihMk);

            String indeks = JOptionPane.showInputDialog("Masukkan nilai indeks (A/B/C/D/E):").toUpperCase();
            if (!indeks.matches("[ABCDE]")) {
                JOptionPane.showMessageDialog(null, "Indeks tidak valid.");
                Menu();
                return;
            }

            // Insert ke database
            if (insertNilaiToDatabase(mhs.nim, mk.kode, indeks)) {
                mhs.indexNilai.put(mk, indeks);
                JOptionPane.showMessageDialog(null, "Nilai berhasil ditambahkan ke database!");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal menambahkan nilai ke database!");
            }
            Menu();
        }

        // SELECT Data dari Database
        public static void lihatData(){
            // Load semua data dari database
            loadAllDataFromDatabase();
            
            if(mhsList.isEmpty()){
                JOptionPane.showMessageDialog(null, "Belum ada data mahasiswa");
                Menu();
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Mahasiswa mhs : mhsList) {
                sb.append("NIM: ").append(mhs.nim)
                .append("\nNama: ").append(mhs.nama)
                .append("\nJurusan: ").append(mhs.jurusan.nama)
                .append("\nNilai Mata Kuliah:\n");
                double totalNilai = 0;
                int totalSks = 0;
                for (Map.Entry<MataKuliah, String> entry : mhs.indexNilai.entrySet()) {
                    MataKuliah mk = entry.getKey();
                    String indeks = entry.getValue();
                    int nilai = 0;
                    switch (indeks) {
                        case "A": nilai = 4; break;
                        case "B": nilai = 3; break;
                        case "C": nilai = 2; break;
                        case "D": nilai = 1; break;
                        case "E": nilai = 0; break;
                    }
                    sb.append("- ").append(mk.nama)
                    .append(" (").append(mk.sks).append(" SKS): ")
                    .append(indeks).append("\n");
                    totalNilai += nilai * mk.sks;
                    totalSks += mk.sks;
                }
                double ip = totalSks > 0 ? totalNilai / totalSks : 0;
                sb.append("IP: ").append(String.format("%.2f", ip)).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, sb.toString());
            Menu();
        }

        // UPDATE Jurusan
        public static void updateJurusan() {
            loadJurusanFromDatabase();
            if (jurusanList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Belum ada jurusan untuk diupdate.");
                Menu();
                return;
            }

            String daftarJurusan = "Pilih Jurusan yang akan diupdate:\n";
            for (int i = 0; i < jurusanList.size(); i++) {
                daftarJurusan += (i + 1) + ". " + jurusanList.get(i).nama + " (" + jurusanList.get(i).kode + ")\n";
            }
            int pilih = Integer.parseInt(JOptionPane.showInputDialog(daftarJurusan)) - 1;
            Jurusan jurusan = jurusanList.get(pilih);

            String namaBaru = JOptionPane.showInputDialog("Masukkan nama jurusan baru:", jurusan.nama);
            
            if (updateJurusanInDatabase(jurusan.kode, namaBaru)) {
                jurusan.nama = namaBaru;
                JOptionPane.showMessageDialog(null, "Jurusan berhasil diupdate!");
            } else {
                JOptionPane.showMessageDialog(null, "Gagal mengupdate jurusan!");
            }
            Menu();
        }

        // DELETE Jurusan
        public static void deleteJurusan() {
            loadJurusanFromDatabase();
            if (jurusanList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Belum ada jurusan untuk dihapus.");
                Menu();
                return;
            }

            String daftarJurusan = "Pilih Jurusan yang akan dihapus:\n";
            for (int i = 0; i < jurusanList.size(); i++) {
                daftarJurusan += (i + 1) + ". " + jurusanList.get(i).nama + " (" + jurusanList.get(i).kode + ")\n";
            }
            int pilih = Integer.parseInt(JOptionPane.showInputDialog(daftarJurusan)) - 1;
            Jurusan jurusan = jurusanList.get(pilih);

            int konfirmasi = JOptionPane.showConfirmDialog(null, 
                "Apakah Anda yakin ingin menghapus jurusan " + jurusan.nama + "?\n" +
                "Ini akan menghapus semua data terkait (mahasiswa, mata kuliah, nilai).", 
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

            if (konfirmasi == JOptionPane.YES_OPTION) {
                if (deleteJurusanFromDatabase(jurusan.kode)) {
                    jurusanList.remove(jurusan);
                    JOptionPane.showMessageDialog(null, "Jurusan berhasil dihapus!");
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menghapus jurusan!");
                }
            }
            Menu();
        }

        // Method untuk INSERT Jurusan ke Database
        private static boolean insertJurusanToDatabase(String kode, String nama) {
            String sql = "INSERT INTO jurusan (kode, nama) VALUES (?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kode);
                pstmt.setString(2, nama);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error inserting jurusan: " + e.getMessage());
                return false;
            }
        }

        // Method untuk INSERT Mahasiswa ke Database
        private static boolean insertMahasiswaToDatabase(String nim, String nama, String kodeJurusan) {
            String sql = "INSERT INTO mahasiswa (nim, nama, kode_jurusan) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nim);
                pstmt.setString(2, nama);
                pstmt.setString(3, kodeJurusan);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error inserting mahasiswa: " + e.getMessage());
                return false;
            }
        }

        // Method untuk INSERT Mata Kuliah ke Database
        private static boolean insertMataKuliahToDatabase(String kode, String nama, int sks, String kodeJurusan) {
            String sql = "INSERT INTO mata_kuliah (kode, nama, sks, kode_jurusan) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kode);
                pstmt.setString(2, nama);
                pstmt.setInt(3, sks);
                pstmt.setString(4, kodeJurusan);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error inserting mata kuliah: " + e.getMessage());
                return false;
            }
        }

        // Method untuk INSERT Nilai ke Database
        private static boolean insertNilaiToDatabase(String nim, String kodeMataKuliah, String indeks) {
            String sql = "INSERT INTO nilai (nim, kode_mata_kuliah, indeks_nilai) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE indeks_nilai = VALUES(indeks_nilai)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nim);
                pstmt.setString(2, kodeMataKuliah);
                pstmt.setString(3, indeks);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error inserting nilai: " + e.getMessage());
                return false;
            }
        }

        // Method untuk UPDATE Jurusan di Database
        private static boolean updateJurusanInDatabase(String kode, String namaBaru) {
            String sql = "UPDATE jurusan SET nama = ? WHERE kode = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, namaBaru);
                pstmt.setString(2, kode);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error updating jurusan: " + e.getMessage());
                return false;
            }
        }

        // Method untuk DELETE Jurusan dari Database
        private static boolean deleteJurusanFromDatabase(String kode) {
            String sql = "DELETE FROM jurusan WHERE kode = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kode);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                System.err.println("Error deleting jurusan: " + e.getMessage());
                return false;
            }
        }

        // Method untuk LOAD Jurusan dari Database
        private static void loadJurusanFromDatabase() {
            jurusanList.clear();
            String sql = "SELECT kode, nama FROM jurusan";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String kode = rs.getString("kode");
                    String nama = rs.getString("nama");
                    Jurusan jurusan = new Jurusan(kode, nama);
                    jurusanList.add(jurusan);
                }
            } catch (SQLException e) {
                System.err.println("Error loading jurusan: " + e.getMessage());
            }
        }

        // Method untuk LOAD Mahasiswa dari Database
        private static void loadMahasiswaFromDatabase() {
            mhsList.clear();
            String sql = "SELECT m.nim, m.nama, m.kode_jurusan, j.nama as nama_jurusan " +
                        "FROM mahasiswa m JOIN jurusan j ON m.kode_jurusan = j.kode";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String nim = rs.getString("nim");
                    String nama = rs.getString("nama");
                    String kodeJurusan = rs.getString("kode_jurusan");
                    String namaJurusan = rs.getString("nama_jurusan");
                    
                    Jurusan jurusan = new Jurusan(kodeJurusan, namaJurusan);
                    Mahasiswa mhs = new Mahasiswa(nim, nama, jurusan);
                    mhsList.add(mhs);
                }
            } catch (SQLException e) {
                System.err.println("Error loading mahasiswa: " + e.getMessage());
            }
        }

        // Method untuk LOAD semua data dari Database
        private static void loadAllDataFromDatabase() {
            // Load jurusan
            loadJurusanFromDatabase();
            
            // Load mahasiswa
            loadMahasiswaFromDatabase();
            
            // Load mata kuliah untuk setiap jurusan
            for (Jurusan jurusan : jurusanList) {
                loadMataKuliahFromDatabase(jurusan);
            }
            
            // Load nilai untuk setiap mahasiswa
            for (Mahasiswa mhs : mhsList) {
                loadNilaiFromDatabase(mhs);
            }
        }

        // Method untuk LOAD Mata Kuliah dari Database
        private static void loadMataKuliahFromDatabase(Jurusan jurusan) {
            jurusan.matkulList.clear();
            String sql = "SELECT kode, nama, sks FROM mata_kuliah WHERE kode_jurusan = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, jurusan.kode);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String kode = rs.getString("kode");
                    String nama = rs.getString("nama");
                    int sks = rs.getInt("sks");
                    MataKuliah mk = new MataKuliah(kode, nama, sks);
                    jurusan.matkulList.add(mk);
                }
            } catch (SQLException e) {
                System.err.println("Error loading mata kuliah: " + e.getMessage());
            }
        }

        // Method untuk LOAD Nilai dari Database
        private static void loadNilaiFromDatabase(Mahasiswa mhs) {
            mhs.indexNilai.clear();
            String sql = "SELECT n.kode_mata_kuliah, n.indeks_nilai, mk.nama, mk.sks " +
                        "FROM nilai n JOIN mata_kuliah mk ON n.kode_mata_kuliah = mk.kode " +
                        "WHERE n.nim = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, mhs.nim);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String kodeMk = rs.getString("kode_mata_kuliah");
                    String indeks = rs.getString("indeks_nilai");
                    String namaMk = rs.getString("nama");
                    int sks = rs.getInt("sks");
                    
                    MataKuliah mk = new MataKuliah(kodeMk, namaMk, sks);
                    mhs.indexNilai.put(mk, indeks);
                }
            } catch (SQLException e) {
                System.err.println("Error loading nilai: " + e.getMessage());
            }
        }
    }

    public static void Menu() {
        StringBuilder menu = new StringBuilder();
        menu.append("Menu Sistem Mahasiswa dengan Database \n")
        .append("0. Tambah Jurusan \n")
        .append("1. Tambah Mahasiswa \n")
        .append("2. Tambah Mata Kuliah \n")
        .append("3. Tambah Nilai \n")
        .append("4. Lihat Data \n")
        .append("5. Update Jurusan \n")
        .append("6. Delete Jurusan \n")
        .append("7. Keluar \n");
        int pilihan = Integer.parseInt(JOptionPane.showInputDialog(menu.toString()));

        switch (pilihan) {
            case 0:
                aList.tambahJurusan();
                break;
            case 1:
                aList.tambahMhs();
                break;
            case 2:
                aList.tambahMatkul();
                break;
            case 3:
                aList.tambahNilai();
                break;
            case 4:
                aList.lihatData();
                break;
            case 5:
                aList.updateJurusan();
                break;
            case 6:
                aList.deleteJurusan();
                break;
            case 7:
                DatabaseConnection.closeConnection();
                System.exit(0);
                break;
            default:
                JOptionPane.showMessageDialog(null, "Pilihan tidak valid");
        }
    }

    public static void main(String[] args) {
        // Test koneksi database
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Gagal koneksi ke database!\n" +
                "Pastikan:\n" +
                "1. PostgreSQL server sudah berjalan\n" +
                "2. Database 'sistem_mahasiswa' sudah dibuat\n" +
                "3. Tabel sudah dibuat sesuai schema\n" +
                "4. Username dan password database benar");
            return;
        }
        
        JOptionPane.showMessageDialog(null, "Koneksi database berhasil!");
        Menu();
    }
}
