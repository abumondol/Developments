function [dist, dtw_paths] = mauth_distance(sign1, sign2)
    
    pp = [];
    pp(1).p1 = sign1.acc_nz(:,1);
    pp(2).p1 = sign1.acc_nz(:,2);
    pp(3).p1 = sign1.acc_nz(:,3);
    pp(4).p1 = sign1.acc_nz(:,4);    
    pp(5).p1 = sign1.gyro_nz(:,1);
    pp(6).p1 = sign1.gyro_nz(:,2);
    pp(7).p1 = sign1.gyro_nz(:,3);
    pp(8).p1 = sign1.gyro_nz(:,4);
    
    pp(1).p2 = sign2.acc_nz(:,1);
    pp(2).p2 = sign2.acc_nz(:,2);
    pp(3).p2 = sign2.acc_nz(:,3);
    pp(4).p2 = sign2.acc_nz(:,4);    
    pp(5).p2 = sign2.gyro_nz(:,1);
    pp(6).p2 = sign2.gyro_nz(:,2);
    pp(7).p2 = sign2.gyro_nz(:,3);
    pp(8).p2 = sign2.gyro_nz(:,4);
    
    dist = zeros(8,1);    
    dtw_paths = [];
    for i=1:8
        s1 = pp(i).p1;
        s2 = pp(i).p2;
        [d, path] = DTW(s1, s2);
        dist(i,1) = d;        
        dtw_paths(i).path = path;        
    end
    
end
