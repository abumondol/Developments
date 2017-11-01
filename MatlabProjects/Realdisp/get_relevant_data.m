function res = get_relevant_data(d)    
    res = [];
    res.t = d(:,1) + d(:,2)/1e6; % first column in second, second column in microsecond
    res.activity = d(:, 120);
    
    for i = 1:9 % total 9 positions
        s = 3 + (i-1)*13; 
        res.pos(i).accel = d(:, s:s+2);
        %res.pos(i).gyro = d(:, s+3:s+5);
    end
    
end