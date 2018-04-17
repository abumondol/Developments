
for s = 1:10
    for ic = 1:5
        fprintf('%d, %d\n', ic, s);
        gen_data(s).ico(ic).selected_windows = [];
        for i = 1:10
            segs = gen_data(s).ico(ic).segments(i).hw;            
            segs = segs(segs(:,2)-segs(:,1)>=200, :);
            a  = segs(:,1)-25;
            b  = segs(:,2)+25;

            fcount = size(features(i).hw, 1);            
            selected = zeros(fcount, 1);
            for j = 1:fcount
                ix = (j-1)*25+1;
                label = sum(ix>=a & ix<=b);                
                selected(j, 1) = label;
            end            
            gen_data(s).ico(ic).selected_windows(i).hw = selected;
 
            segs = gen_data(s).ico(ic).segments(i).eat;            
            segs = segs(segs(:,2)-segs(:,1)>=200, :);
            a  = segs(:,1)-25;
            b  = segs(:,2)+25;
            fcount = size(features(i).eat, 1);            
            selected = zeros(fcount, 1);
            for j = 1:fcount
                ix = (j-1)*25+1;
                label = sum(ix>=a & ix<=b);                
                selected(j, 1) = label;
            end
            gen_data(s).ico(ic).selected_windows(i).eat = selected; 
          
        end

    end
end

save('gen_data', 'gen_data');