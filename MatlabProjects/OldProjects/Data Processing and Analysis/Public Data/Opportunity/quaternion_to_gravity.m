function Rz = quaternion_to_gravity(q)
        q0 = q(:,1); %qw
        q1 = q(:, 2); %qx
        q2 = q(:, 3); %qy
        q3 = q(:, 4); %qz
        %sin2_theta_by_2 = q1*q1 + q2*q2 + q3*q3;        
        %q0 = sqrt(1-sin2_theta_by_2);
        
        %Rx = [q0.*q0+q1.*q1-q2.*q2-q3.*q3, 2*(q1.*q2 - q0.*q3),  2*(q1.*q3+q0.*q2)];
        %Ry = [2*(q0.*q3+q1.*q2),  q0.*q0-q1.*q1+q2.*q2-q3.*q3, 2*(q2.*q3 - q0.*q1)];
        Rz = [2*(q1.*q3 - q0.*q2), 2*(q2.*q3+q0.*q1), q0.*q0-q1.*q1-q2.*q2+q3.*q3 ];
end