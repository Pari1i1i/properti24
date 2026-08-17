package property24.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import property24.entity.User;
import property24.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Validasi login – return Optional kosong kalau gagal.
     */
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            User user = found.get();
            // Support plain-text password (legacy) dan BCrypt hash
            boolean match;
            try {
                match = passwordEncoder.matches(rawPassword, user.getPassword());
            } catch (Exception e) {
                // Kalau hash format salah, coba plain-text compare
                match = rawPassword.equals(user.getPassword());
            }
            if (match) return Optional.of(user);
        }
        return Optional.empty();
    }

    /**
     * Registrasi user baru dengan role = user dan password di-hash BCrypt.
     */
    public User register(String namaLengkap, String email, String rawPassword) {
        if (userRepository.existsByUsername(email)) {
            throw new IllegalArgumentException("Email sudah terdaftar, silakan gunakan email lain.");
        }
        User user = new User();
        user.setNamaLengkap(namaLengkap);
        user.setUsername(email);           // email dipakai sebagai username
        user.setEmail(email);              // set field email juga
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(User.Role.user);
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    /** Admin: ambil semua user, urutkan by id desc (terbaru duluan) */
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "id"));
    }

    /** Admin: hapus user by id */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
