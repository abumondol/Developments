function ix = search_subject_index(res, subject, index)
    ix = 0;
    count = length(res);
    for i=1:count
        if res(i).subject == subject && res(i).index == index
            ix = i;
            return;
        end
    end    

end