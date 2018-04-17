window_size = 50;

for s = 1:10
    fprintf('%d\n', s);
    ai = data(s).annot_indices;    
    a  = ai(:,1);
    b  = ai(:,2);
    
    count = length(data(s).hw_data);
    ix = 25:50:count-25;
    ix = ix';
    w = [];
    for ic = 1:5
        ca = cellassign(ic).subject(s).hw_ca;
        w = [w, ca(ix)];
    end

    xyz = data(s).hw_data(ix, 2:4);

    size(w)
    len = length(ix)
    labels = zeros(len, 1);
    for j=1:len        
        p = find(ix(j)>= a & ix(j)<=b);
        if ~isempty(p)
            labels(j) = 1;
        end
    end    
    w = [w, labels];
    gen_data(s).windows_hw = w;

    xyz = [xyz, labels];
    gen_data(s).xyz_hw = xyz;


    count = length(data(s).eat_data);
    ix = 25:50:count-25;
    ix = ix';
    w = [];
    for ic = 1:5
        ca = cellassign(ic).subject(s).eat_ca;
        w = [w, ca(ix)];
    end
    xyz = data(s).eat_data(ix, 2:4);
    
    labels = zeros(length(ix), 1);    
    w = [w, labels];
    gen_data(s).windows_eat = w;
    
     xyz = [xyz, labels];
    gen_data(s).xyz_eat = xyz;

end

save('gen_data', 'gen_data');