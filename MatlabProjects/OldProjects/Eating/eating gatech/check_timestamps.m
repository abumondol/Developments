load('raw_data');

for i=1:20
    d = raw_data(i).acl;
    t = d(:,1);
    count = length(d);
    for j= 2:count
        if t(j) < t(j-1)
            fprintf('%d, %d, %f\n', i, j, t(j));
        end
    end
end