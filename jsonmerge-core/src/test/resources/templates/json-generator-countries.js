{
  countries: [
    '{{repeat(2, 3)}}',
    {
      name: '{{country()}}',
      users: [
        '{{repeat(1, 3)}}',
        {
          id: '{{objectId()}}',
          isActive: '{{bool()}}',
          balance: '{{floating(50, 4000, 2, "$0,0.00")}}',
          age: '{{integer(20, 40)}}',
          name: '{{firstName()}} {{surname()}}',
          company: '{{company()}}',
          email: '{{email()}}',
          registered: '{{date(new Date(2017, 0, 1), new Date(), "YYYY-MM-ddThh:mm:ss Z")}}',
          tags: [
            '{{repeat(0,2)}}',
            '{{lorem(1, "words")}}'
          ],
          friends: [
            '{{repeat(0,3)}}',
            {
              id: '{{objectId()}}',
              name: '{{firstName()}} {{surname()}}'
            }
          ]
        }
      ]
    }
  ]
}