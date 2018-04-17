function t = quaternion_multiplication(q, r)
    q0 = q(:,1);
    q1 = q(:, 2);
    q2 = q(:, 3);
    q3 = q(:, 4);
    
    r0 = r(:,1);
    r1 = r(:, 2);
    r2 = r(:, 3);
    r3 = r(:, 4);
    
    t0 = r0.*q0 - r1.*q1 - r2.*q2 - r3.*q3;
    t1 = r0.*q1 + r1.*q0 - r2.*q3 + r3.*q2;
    t2 = r0.*q2 + r1.*q3 + r2.*q0 - r3.*q1;
    t3 = r0.*q3 - r1.*q2 + r2.*q1 + r3.*q0;
    
    t = [t0 t1 t2 t3];
end