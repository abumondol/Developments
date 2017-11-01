sec = 1;
win_size = sec * 50;

features = [];

ft = [];
gpoints =[];
sub_act = [];
label_counts = [];

for sub = 1:sub_count
    sess_count = length(realdisp_data(sub).session);
    for sess = 1:sess_count
        fprintf('sub:%d, sess:%d\n', sub, sess);
        labels = realdisp_data(sub).session(sess).labels;        
        count = length(labels);        
        feature_count = length(1:win_size/2:count-win_size+1);
        
        f = zeros(feature_count, 84);            
        gp = zeros(feature_count, 18);
        sact = zeros(feature_count, 2);

        a1 = realdisp_data(sub).session(sess).position(1).accel;                
        g1 = realdisp_data(sub).session(sess).position(1).grav;                
        a2 = realdisp_data(sub).session(sess).position(2).accel;                
        g2 = realdisp_data(sub).session(sess).position(2).grav;                

        i = 1;
        for s =1:win_size/2:count-win_size+1
            e = s + win_size - 1;
            m = (s+e-1)/2;
            
            act = mode(labels(s:e, 1));
            sact(i, :) = [sub, act];
            
            f(i, :) = [features_one_window(a1(s:e, :)), features_one_window(a2(s:e, :))];                
            gp(i, :) = [g1(s, :), g1(m, :), g1(e, :), g2(s, :), g2(m, :), g2(e, :),];
                        
            i = i+1;                
        end
        
        ft = [ft; f];
        gpoints = [gpoints; gp];
        sub_act = [sub_act; sact];
        
        lc = zeros(34, 3);
        for i=0:33
            lc(i+1, :) =[sub, i, sum(sact(:,2)==i)] ;
        end
        
        label_counts = [label_counts; lc];
        
    end    
end

features(sec).features = ft;
features(sec).gpoints = gpoints;
features(sec).sub_act = sub_act;
features(sec).label_counts = label_counts;

save('features', 'features');
