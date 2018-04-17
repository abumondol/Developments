function [dist, dtw_paths] = airsign_distance(sign1, sign2)
    
    pp = [];
    pp(1).p1 = sign1.acc_ppx;
    pp(2).p1 = sign1.acc_ppy;
    pp(3).p1 = sign1.acc_ppz;
    pp(4).p1 = sign1.acc_ppm;    
    pp(5).p1 = sign1.gyro_ppx;
    pp(6).p1 = sign1.gyro_ppy;
    pp(7).p1 = sign1.gyro_ppz;
    pp(8).p1 = sign1.gyro_ppm;
    
    pp(1).p2 = sign2.acc_ppx;
    pp(2).p2 = sign2.acc_ppy;
    pp(3).p2 = sign2.acc_ppz;
    pp(4).p2 = sign2.acc_ppm;    
    pp(5).p2 = sign2.gyro_ppx;
    pp(6).p2 = sign2.gyro_ppy;
    pp(7).p2 = sign2.gyro_ppz;
    pp(8).p2 = sign2.gyro_ppm;
    
    dist = zeros(8,1);    
    dtw_paths = [];
    for i=1:8
        s1 = pp(i).p1(:,2);
        s2 = pp(i).p2(:,2);
        [d, path] = DTW(s1, s2);
        dist(i,1) = d;        
        dtw_paths(i).path = path;        
    end
    
end
