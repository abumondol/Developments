data = opp_data;
sub_count = length(data);

codes = [404505;404508;404511;404516;404517;404519;404520;405506;406505;406508;406511;406516;406517;406519;406520;407521;408512];
res = [];
code = 404516;
for i=1:length(codes)
    code = codes(i);
    for sub = 1:sub_count
        lc = 0;
        rc = 0;
        rlc = 0;
        sess_count = length(data(sub).session);
        for sess = 1:sess_count                   
            c = opp_activity_chunks(sub).session(sess).chunks_ml;
            a = c(c(:,3)==code & c(:, 4) == 1, 3);
            rc = rc + length(a);
            a = c(c(:,3)==code & c(:, 5) == 1, 3);
            lc = lc + length(a);            
            a = c(c(:,3)==code & c(:, 4) == 1 & c(:, 5) == 1, 3);
%             if code == 407521 && ~isempty(a)
%                 sub
%                 sess
%                 a
%                 return
%             end
            rlc = rlc + length(a);
        end        
        res = [res; code, sub, rc, lc, rlc];
    end
end